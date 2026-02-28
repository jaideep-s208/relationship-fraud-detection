package com.example.relationshipsystem.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

@Data
@RelationshipProperties
public class UserTransactionRelation {

    @Id
    @GeneratedValue
    private Long id;

    private String type;

    @TargetNode
    private Transaction transaction;
}