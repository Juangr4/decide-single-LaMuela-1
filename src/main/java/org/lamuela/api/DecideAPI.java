package org.lamuela.api;

import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.lamuela.Decide;
import org.lamuela.api.models.*;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.*;
import java.util.List;

public class DecideAPI {

    private DecideAPI(){}

    private static String contentType = "application/json; charset=utf-8";
    private static String authHeader = "Authorization";

    private static String getTokenHeader(String token) {
        return "Token " + token;
    }

    private static String adminToken;

    private static String getAdminToken() {
        if(adminToken == null)  adminToken = login(Decide.getEnvVariable("DECIDE_USER"), Decide.getEnvVariable("DECIDE_PASSWORD")).getToken();
        return adminToken;
    }

    private static <T> T getModel(String json, Type model) {
        Gson gson = new Gson();
        return gson.fromJson(json, model);
    }

    public static LoginResponse login(String username, String password) {
        HttpResponse<String> response = Unirest.post("/authentication/login/")
                .field("username", username)
                .field("password", password)
                .asString();
        return getModel(response.getBody(), LoginResponse.class);
    }

    public static LoginResponse register(String username, String password) {
        HttpResponse<String> response = Unirest.post("/authentication/register/")
                .field("username", username)
                .field("password", password)
                .field("token", getAdminToken())
                .asString();
        return getModel(response.getBody(), LoginResponse.class);
    }

    public static User getUser(String token) {
        HttpResponse<String> response = Unirest.post("/authentication/getuser/")
                .contentType(contentType)
                .body(String.format("{\"token\":\"%s\"}", token))
                .asString();
        return getModel(response.getBody(), User.class);
    }

    public static Voting[] getAllVotings() {
        HttpResponse<String> response = Unirest.get("/voting/").asString();
        return getModel(response.getBody(), Voting[].class);
    }

    public static Voting getVotingById(int id) {
        HttpResponse<String> response = Unirest.get(String.format("/voting/?id=%d", id))
                .asString();
        Voting[] votings = (Voting[]) getModel(response.getBody(), Voting[].class);
        return votings.length > 0 ? votings[0] : null;
    }

    public static void createVoting(String name, String description, String question, List<String> options) {
        Unirest.post("/voting/")
                .header(authHeader, getTokenHeader(getAdminToken()))
                .contentType(contentType)
                .body(String.format(
                        "{\"name\":\"%s\",\"desc\":\"%s\",\"question\":\"%s\",\"question_opt\":%s}",
                        name,
                        description,
                        question,
                        options.stream().map(i -> "\"" + i + "\"").toList()))
                .asString();
    }

    public static String updateVoting(Voting voting, String action) {
        HttpResponse<String> response = Unirest.put(String.format("/voting/%d/", voting.getId()))
                .header(authHeader, getTokenHeader(getAdminToken()))
                .contentType(contentType)
                .body(String.format("{\"action\":\"%s\"}", action))
                .asString();
        return response.getBody();
    }

    public static String addCensus(Voting voting, List<User> users) {
        HttpResponse<String> response = Unirest.post("/census/")
                .header(authHeader, getTokenHeader(getAdminToken()))
                .contentType(contentType)
                .body(String.format(
                        "{\"voting_id\":%d,\"voters\":%s}",
                        voting.getId(),
                        users.stream().map(User::getId).toList()
                ))
                .asString();
        return response.getBody();
    }

    public static String performVote(Voting voting, Option option, String voterToken) {
        BigInteger a;
        BigInteger b;
        try {
            BigInteger p = voting.getPubKey().getP();
            BigInteger g = voting.getPubKey().getG();
            BigInteger y = voting.getPubKey().getY();
            SecureRandom random = new SecureRandom();
            BigInteger k;
            do {
                k = new BigInteger(p.bitLength(), random);
            }while (k.compareTo(p.subtract(BigInteger.TWO)) >= 0);
            a = g.modPow(k, p);
            b = y.modPow(k, p).multiply(BigInteger.valueOf(option.getNumber())).mod(p);
        } catch (Exception e) {
            return null;
        }
        HttpResponse<String> response = Unirest.post("/gateway/store/")
                .header(authHeader, getTokenHeader(voterToken))
                .contentType(contentType)
                .body(String.format(
                        "{\"vote\":{\"a\":%s,\"b\":%s},\"voting\":%d,\"voter\":%d,\"token\":\"%s\"}",
                        a,
                        b,
                        voting.getId(),
                        getUser(voterToken).getId(),
                        voterToken
                ))
                .asString();
        return response.getBody();
    }

}
