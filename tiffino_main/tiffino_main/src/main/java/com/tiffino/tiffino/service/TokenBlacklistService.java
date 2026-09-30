package com.tiffino.tiffino.service;

/**

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {

    private Set<String> blacklist = new HashSet<>();

    public void blacklistToken(String token) {
        blacklist.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}
**/



import org.springframework.stereotype.Service;

import java.util.*;
/**
@Service
public class TokenBlacklistService {

    private final Set<String> blacklist = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, Set<String>> userTokensMap = Collections.synchronizedMap(new HashMap<>());

    // Blacklist a single token
    public void blacklistToken(String token, String username) {
        blacklist.add(token);
        userTokensMap.computeIfAbsent(username, k -> new HashSet<>()).add(token);
    }

    // Check if token is blacklisted
    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }

    // Blacklist all tokens of a user (for SuperAdmin update)
    public void blacklistTokensOfUser(String username) {
        Set<String> tokens = userTokensMap.get(username);
        if (tokens != null) {
            blacklist.addAll(tokens);
            tokens.clear();
        }
    }
}
**/


import org.springframework.stereotype.Service;
/**
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class TokenBlacklistService {

    private final Set<String> blacklist = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, Set<String>> userTokensMap = Collections.synchronizedMap(new HashMap<>());

    // Add token to a user's active list
    public void addTokenForUser(String token, String username) {
        userTokensMap.computeIfAbsent(username, k -> new HashSet<>()).add(token);
    }

    // Blacklist a single token
    public void blacklistToken(String token, String username) {
        blacklist.add(token);
        // remove from user's active token list
        Set<String> tokens = userTokensMap.get(username);
        if (tokens != null) {
            tokens.remove(token);
        }
    }

    // Check if token is blacklisted
    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }

    // Blacklist all tokens of a user (for password update, logout all)
    public void blacklistTokensOfUser(String username) {
        Set<String> tokens = userTokensMap.get(username);
        if (tokens != null) {
            blacklist.addAll(tokens);
            tokens.clear();
        }
    }
}
**/


import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class TokenBlacklistService {

    private final Set<String> blacklist = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, Set<String>> userTokensMap = Collections.synchronizedMap(new HashMap<>());

    // Track issued token per user
    public void addTokenForUser(String token, String username) {
        userTokensMap.computeIfAbsent(username, k -> new HashSet<>()).add(token);
    }

    // Blacklist a single token
    public void blacklistToken(String token, String username) {
        blacklist.add(token);
        Set<String> tokens = userTokensMap.get(username);
        if (tokens != null) {
            tokens.remove(token);
        }
        System.out.println("Blacklisted token: " + token);
    }

    // Blacklist all tokens of a user (SuperAdmin update)
    public void blacklistTokensOfUser(String username) {
        Set<String> tokens = userTokensMap.get(username);
        if (tokens != null) {
            blacklist.addAll(tokens);
            tokens.clear();
        }
        System.out.println("All tokens blacklisted for user: " + username);
    }

    // Check if token is blacklisted
    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}
