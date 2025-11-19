package org.igdevx.shopservice.elasticsearch.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "products")
public class ProductDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Keyword)
    private String currencyCode;

    @Field(type = FieldType.Long)
    private Long currencyId;

    @Field(type = FieldType.Keyword)
    private String unitName;

    @Field(type = FieldType.Long)
    private Long unitId;

    @Field(type = FieldType.Keyword)
    private String shelfName;

    @Field(type = FieldType.Long)
    private Long shelfId;

    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Keyword)
    private Set<String> certificationNames;

    @Field(type = FieldType.Long)
    private Set<Long> certificationIds;

    @Field(type = FieldType.Keyword)
    private String mainImageId;

    @Field(type = FieldType.Keyword)
    private String mainImageUrl;

    @Field(type = FieldType.Boolean)
    private Boolean isFresh;

    @Field(type = FieldType.Long)
    private Long producerId;

    @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSSSS||uuuu-MM-dd")
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSSSS||uuuu-MM-dd")
    private LocalDateTime updatedAt;

    @Field(type = FieldType.Boolean)
    private Boolean isDeleted;
}

