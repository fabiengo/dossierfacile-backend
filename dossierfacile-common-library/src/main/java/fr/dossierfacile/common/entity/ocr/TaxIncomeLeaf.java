package fr.dossierfacile.common.entity.ocr;

import fr.dossierfacile.common.enums.ParsedFileClassification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaxIncomeLeaf implements ParsedFile {
    @Builder.Default
    ParsedFileClassification classification = ParsedFileClassification.TAX_INCOME_LEAF;
    String numeroFiscal; // declarant1
    String anneeDesRevenus;
    Integer pageCount;
    Integer page;
}