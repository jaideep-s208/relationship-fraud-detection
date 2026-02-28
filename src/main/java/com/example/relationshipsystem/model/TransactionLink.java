package com.example.relationshipsystem.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;


@Data
@RelationshipProperties
public class TransactionLink {

    @Id
    @GeneratedValue
    private Long id;
    private String type; // SHARED_DEVICE or SHARED_IP
    @TargetNode
    private Transaction targetTransaction;

}