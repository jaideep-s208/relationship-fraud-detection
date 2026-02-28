package com.example.relationshipsystem.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.*;

@Data
@EqualsAndHashCode(exclude = "targetUser")
@ToString(exclude = "targetUser")
@RelationshipProperties
public class UserToUserRelation {

    @Id
    @GeneratedValue
    private Long id;

    private String type;
    private double amount;

    @TargetNode
    private User targetUser;
}
