package io.pinnacl.academics.application.data.domain;

import io.pinnacl.commons.data.domain.catalog.ImageObject;

public record Documents(ImageObject picture, ImageObject birthCertificate,
                        ImageObject formerSchoolsReportCard,
                        ImageObject applicationFormsPaymentReceipt,
                        ImageObject entranceExamResult) {
}
