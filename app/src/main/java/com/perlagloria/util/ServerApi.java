package com.perlagloria.util;

public class ServerApi {
    public static final String serverHostUrl = "http://perlagloria.com";

    public static final String loadCustomersUrl = serverHostUrl + "/customer/getcustomers";
    public static final String loadTournamentUrl = serverHostUrl + "/tournament/gettournaments?customerId=";
    public static final String loadDivisionUrl = serverHostUrl + "/division/getdivisions?tournamentId=";
    public static final String loadTeamUrl = serverHostUrl + "/team/getteams?divisionId=";
    public static final String loadFixtureMatchInfoUrl = serverHostUrl + "/fixturematch/getnextfixturematch?teamId=";
    public static final String loadStatisticsUrl = serverHostUrl + "/team/getpositionsteams?teamId=";
    public static final String loadTeamImageUrl = serverHostUrl + "/team/getteamimage?teamId=";
    public static final String loadFixtureMatchMapImageUrl = serverHostUrl + "/fixturematch/getnextfixturematchmapimage?teamId=";
}
