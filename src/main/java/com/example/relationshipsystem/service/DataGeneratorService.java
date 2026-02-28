package com.example.relationshipsystem.service;

import com.example.relationshipsystem.model.Transaction;
import com.example.relationshipsystem.model.User;
import com.example.relationshipsystem.repository.TransactionRepository;
import com.example.relationshipsystem.repository.UserRepository;
import com.example.relationshipsystem.service.TransactionService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DataGeneratorService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    private final Random random = new Random();

    public DataGeneratorService(UserRepository userRepository,
                                TransactionRepository transactionRepository, TransactionService transactionService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.transactionService=transactionService;
    }

    /* =====================================================
       RESET DATABASE
    ====================================================== */

    public void clearDatabase() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();
    }

    /* =====================================================
       DEMO DATA ()
    ====================================================== */

    public String generateDemoData() {

        clearDatabase();

        // ---- USERS (7 Users with Shared Attributes) ----
        User u1 = createUser("U1","Alice","90001","shared@test.com","City1");
        User u2 = createUser("U2","Bob","90001","bob@test.com","City2");          // shared phone
        User u3 = createUser("U3","Charlie","90003","shared@test.com","City3");  // shared email
        User u4 = createUser("U4","David","90004","d@test.com","City4");
        User u5 = createUser("U5","Eva","90005","eva@test.com","City5");
        User u6 = createUser("U6","Frank","90005","frank@test.com","City1");     // shared phone
        User u7 = createUser("U7","Grace","90007","grace@test.com","City2");

        List<User> users = userRepository.saveAll(
                List.of(u1,u2,u3,u4,u5,u6,u7)
        );

        // ---- TRANSACTIONS (15 Structured) ----
        String[] suspiciousDevices = {"DEVICE-X","DEVICE-Y"};
        String[] suspiciousIps = {"10.0.0.1","10.0.0.2"};

        for(int i=1;i<=15;i++){

            User from = users.get(random.nextInt(users.size()));
            User to = users.get(random.nextInt(users.size()));

            Transaction tx = new Transaction();
            tx.setId("T"+i);
            tx.setFromUserId(from.getId());
            tx.setToUserId(to.getId());
            tx.setAmount(1000 + random.nextInt(100000));

            // intentionally create clusters
            if(i <= 5){
                tx.setDeviceId("DEVICE-X");
                tx.setIpAddress("10.0.0.1");
            }
            else if(i <= 10){
                tx.setDeviceId("DEVICE-Y");
                tx.setIpAddress("10.0.0.2");
            }
            else{
                tx.setDeviceId("DEVICE-"+i);
                tx.setIpAddress("10.0.0."+i);
            }

            transactionService.save(tx);
        }

        return "Demo dataset created (7 users + 15 transactions)";
    }

    private User createUser(String id,String name,String phone,String email,String city){
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setPhone(phone);
        u.setEmail(email);
        u.setAddress(city);
        return u;
    }

    /* =====================================================
       LARGE DATASET (100,000 Transactions)
    ====================================================== */

    public String generateLargeData(int txCount){

        List<User> users = userRepository.findAll();

        if(users.isEmpty()){
            return "Please generate demo users first.";
        }

        String[] devices = new String[50];
        for(int i=0;i<50;i++){
            devices[i] = "DEVICE-"+i;
        }

        String[] ips = new String[100];
        for(int i=0;i<100;i++){
            ips[i] = "10.0.0."+i;
        }

        List<Transaction> batch = new ArrayList<>();

        for(int i=1;i<=txCount;i++) {

            User from = users.get(random.nextInt(users.size()));
            User to = users.get(random.nextInt(users.size()));

            Transaction tx = new Transaction();
            tx.setId("LT" + i);
            tx.setFromUserId(from.getId());
            tx.setToUserId(to.getId());
            tx.setAmount(100 + random.nextInt(100000));

            tx.setDeviceId(devices[random.nextInt(devices.length)]);
            tx.setIpAddress(ips[random.nextInt(ips.length)]);

            transactionService.save(tx);
            if (i % 1000 == 0) {
                System.out.println("Generated:" + i);
            }
        }
        return txCount + " large transactions generated.";
    }
    public String generateUsersIfEmpty(int usersCount) {

        // check existing users
        long existingUsers = userRepository.count();

        if (existingUsers > 0) {
            return "Users already exist. Skipping generation.";
        }

        List<User> users = new ArrayList<>();

        for (int i = 1; i <= usersCount; i++) {

            User user = new User();
            user.setId("U" + i);
            user.setName("User " + i);
            user.setEmail("user" + i + "@test.com");
            user.setPhone("90000" + i);
            user.setAddress("City" + (i % 5));

            users.add(user);
        }

        userRepository.saveAll(users);

        return usersCount + " users generated successfully.";
    }
}