package com.example.relationshipsystem.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.neo4j.core.schema.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.relationshipsystem.model.UserToUserRelation;
import com.example.relationshipsystem.model.UserTransactionRelation;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = {"sentUsers", "transactions"})
@ToString(exclude = {"sentUsers", "transactions"})
@Node("User")
public class User {

    @Id
    private String id;

    private String name;
    private String email;
    private String phone;
    private String address;

    @Relationship(type = "SENT_TO")
    @JsonIgnore
    private List<UserToUserRelation> sentUsers = new ArrayList<>();

    @Relationship(type = "PARTICIPATED_IN")
    @JsonIgnore
    private List<UserTransactionRelation> transactions = new ArrayList<>();
}