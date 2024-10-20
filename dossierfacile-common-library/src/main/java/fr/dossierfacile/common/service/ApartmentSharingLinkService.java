package fr.dossierfacile.common.service;

import fr.dossierfacile.common.entity.ApartmentSharing;
import fr.dossierfacile.common.entity.ApartmentSharingLink;
import fr.dossierfacile.common.entity.Tenant;
import fr.dossierfacile.common.model.ApartmentSharingLinkModel;
import fr.dossierfacile.common.repository.ApartmentSharingLinkRepository;
import fr.dossierfacile.common.service.interfaces.LinkLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import static fr.dossierfacile.common.enums.ApartmentSharingLinkType.MAIL;
import static fr.dossierfacile.common.enums.LinkType.*;

@Service
@Slf4j
@AllArgsConstructor
public class ApartmentSharingLinkService {

    private final ApartmentSharingLinkRepository apartmentSharingLinkRepository;
    private final LinkLogService linkLogService;

    public List<ApartmentSharingLinkModel> getLinksByMail(ApartmentSharing apartmentSharing) {
        return apartmentSharingLinkRepository.findByApartmentSharingAndLinkType(apartmentSharing, MAIL)
                .stream()
                .map(link -> mapApartmentSharingLink(link, apartmentSharing))
                .toList();
    }

    private ApartmentSharingLinkModel mapApartmentSharingLink(ApartmentSharingLink link, ApartmentSharing apartmentSharing) {
        LocalDateTime lastVisit = linkLogService.getLastVisit(link.getToken(), apartmentSharing).orElse(null);
        return ApartmentSharingLinkModel.builder()
                .id(link.getId())
                .creationDate(link.getCreationDate())
                .ownerEmail(link.getEmail())
                .lastVisit(lastVisit)
                .enabled(!link.isDisabled())
                .fullData(link.isFullData())
                .build();
    }

    public void updateStatus(Long linkId, boolean enabled, ApartmentSharing apartmentSharing) {
        var link = apartmentSharingLinkRepository.findByIdAndApartmentSharing(linkId, apartmentSharing).orElseThrow(NotFoundException::new);
        link.setDisabled(!enabled);
        linkLogService.createNewLog(link, enabled ? ENABLED_LINK : DISABLED_LINK);
        apartmentSharingLinkRepository.save(link);
    }

    public void delete(Long linkId) {
        var link = apartmentSharingLinkRepository.findById(linkId).orElseThrow(NotFoundException::new);
        log.info("Delete token: " + link.getToken() + " by " + link.getLinkType() + " on apartmentSharing" + link.getApartmentSharing().getId());
        linkLogService.createNewLog(link, DELETED_LINK_TOKEN);
        apartmentSharingLinkRepository.deleteById(link.getId());
    }

    public void delete(Long linkId, Tenant tenant) {
        boolean hasAccess = tenant.getApartmentSharing().getApartmentSharingLinks().stream()
                .anyMatch(link -> link.getId().equals(linkId));
        if (hasAccess) {
            delete(linkId);
        }
    }

}
