package org.lamuela;

public class Storage {

    private Storage() {}

    private static String adminChannelId;

    private static String votingCategoryId;

    private static String adminRoleId;

    public static String getAdminChannelId() {
        return adminChannelId;
    }

    public static void setAdminChannelId(String adminChannelId) {
        Storage.adminChannelId = adminChannelId;
    }

    public static String getVotingCategoryId() {
        return votingCategoryId;
    }

    public static void setVotingCategoryId(String votingCategoryId) {
        Storage.votingCategoryId = votingCategoryId;
    }

    public static String getAdminRoleId() {
        return adminRoleId;
    }

    public static void setAdminRoleId(String adminRoleId) {
        Storage.adminRoleId = adminRoleId;
    }
}
