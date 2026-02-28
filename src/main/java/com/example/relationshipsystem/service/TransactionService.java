package com.example.relationshipsystem.service;

import com.example.relationshipsystem.model.*;
import com.example.relationshipsystem.repository.TransactionRepository;
import com.example.relationshipsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public Transaction save(Transaction transaction) {

        Transaction savedTx = transactionRepository.save(transaction);

        Optional<User> fromUserOpt =
                userRepository.findById(transaction.getFromUserId());

        Optional<User> toUserOpt =
                userRepository.findById(transaction.getToUserId());

        if (fromUserOpt.isPresent() && toUserOpt.isPresent()) {

            User fromUser = fromUserOpt.get();
            User toUser = toUserOpt.get();

            UserToUserRelation relation = new UserToUserRelation();
            relation.setTargetUser(toUser);
            relation.setAmount(transaction.getAmount());
            relation.setType("SENT_TO");

            fromUser.getSentUsers().add(relation);

            userRepository.save(fromUser);

            // Find transactions with same device
            var sameDeviceTx =
                    transactionRepository.findByDeviceId(transaction.getDeviceId());

            for (Transaction tx : sameDeviceTx) {
                if (!tx.getId().equals(savedTx.getId())) {

                    TransactionLink link = new TransactionLink();
                    link.setTargetTransaction(tx);
                    link.setType("SHARED_DEVICE");

                    savedTx.getLinkedTransactions().add(link);
                }
            }

// Find transactions with same IP
            var sameIpTx =
                    transactionRepository.findByIpAddress(transaction.getIpAddress());

            for (Transaction tx : sameIpTx) {
                if (!tx.getId().equals(savedTx.getId())) {

                    TransactionLink link = new TransactionLink();
                    link.setTargetTransaction(tx);
                    link.setType("SHARED_IP");

                    savedTx.getLinkedTransactions().add(link);
                }
            }

            transactionRepository.save(savedTx);
        }

        fromUserOpt.ifPresent(user -> {
            UserTransactionRelation rel = new UserTransactionRelation();
            rel.setTransaction(savedTx);
            rel.setType("SENT");

            user.getTransactions().add(rel);
            userRepository.save(user);
        });

        toUserOpt.ifPresent(user -> {
            UserTransactionRelation rel = new UserTransactionRelation();
            rel.setTransaction(savedTx);
            rel.setType("RECEIVED");

            user.getTransactions().add(rel);
            userRepository.save(user);
        });

        return savedTx;
    }
    public List<Transaction> getAll() {
        return(List<Transaction>) transactionRepository.findAll();
    }
}