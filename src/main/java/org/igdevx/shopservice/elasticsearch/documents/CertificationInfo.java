package org.igdevx.shopservice.elasticsearch.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationInfo {

    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Keyword)
    private String label;
}

