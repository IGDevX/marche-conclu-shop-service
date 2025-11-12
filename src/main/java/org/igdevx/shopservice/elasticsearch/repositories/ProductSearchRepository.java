package org.igdevx.shopservice.elasticsearch.repositories;

import org.igdevx.shopservice.elasticsearch.documents.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {

    List<ProductDocument> findByProducerIdAndIsDeletedFalse(Long producerId);

    List<ProductDocument> findByIsDeletedFalse();

    List<ProductDocument> findByProducerIdAndIsDeletedTrue(Long producerId);
}

