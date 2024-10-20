package fr.dossierfacile.process.file.service.parsers;

import fr.dossierfacile.common.entity.ocr.ParsedFile;
import fr.dossierfacile.process.file.service.parsers.tools.PageExtractorModel;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.util.CollectionUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractPDFImageParser<T extends ParsedFile> extends AbstractSinglePageImageOcrParser<T> implements FileParser<T> {
    private transient volatile Tesseract tesseract;

    protected abstract String getJsonModelFile();

    protected abstract T getResultFromExtraction(Map<String, String> extractedText);

    void init() {
        if (tesseract == null) {
            this.tesseract = new Tesseract();
            this.tesseract.setLanguage("fra+eng");
            this.tesseract.setOcrEngineMode(ITessAPI.TessOcrEngineMode.OEM_LSTM_ONLY);
            this.tesseract.setVariable("user_defined_dpi", "128");
        }
    }

    @Override
    public T parse(BufferedImage image /*, int page */) {
        try {
            init();
            PageExtractorModel model = new PageExtractorModel(getJsonModelFile());

            if (modelMatches(model, image)) {
                Map<String, String> extractedTexts = new HashMap<>();
                double scale = image.getWidth() / model.getDefaultWidth();
                for (Map.Entry<String, Rectangle> entry : model.getNamedZones(scale).entrySet()) {
                    extractedTexts.put(entry.getKey(), this.tesseract.doOCR(image, entry.getValue()));
                }
                return getResultFromExtraction(extractedTexts);
            }

            return null;
        } catch (Exception e) {
            log.error("Unable to parse");
            throw new RuntimeException(e);
        }
    }

    protected boolean modelMatches(PageExtractorModel model, BufferedImage image) throws IOException {
        double scale = image.getWidth() / model.getDefaultWidth();

        List<PageExtractorModel.Zone> matchingZones = model.getMatchingZones(scale).stream()
                .filter(zone -> zone.pageFilter() == null)
                .toList();
        if (!CollectionUtils.isEmpty(matchingZones)) {
            return matchingZones.stream().allMatch(
                    (zone) -> {
                        try {
                            String text = this.tesseract.doOCR(image, zone.rect());
                            return text != null && text.trim().matches(zone.regexp());
                        } catch (Exception e) {
                            return false;
                        }
                    }
            );
        }
        return true;
    }
}
