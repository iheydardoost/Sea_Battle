package model;

public class Packet {
    private static int lastRequestID = 0;
    private int requestID;
    private int authToken;
    private boolean authTokenAvailable;
    private PacketType packetType;
    private String body;

    public Packet(PacketType packetType, String body, int authToken, boolean authTokenAvailable) {
        this.packetType = packetType;
        this.body = body;
        this.authToken = authToken;
        this.authTokenAvailable = authTokenAvailable;
        lastRequestID++;
        this.requestID = lastRequestID;

    }

    public String getPacketStr(){
        String str = requestID + ",";
        if(authTokenAvailable)
            str += (authToken + ",");
        else
            str += "null,";
        str += (packetType.toString()+","+body+",$");
        return str;
    }

    public static Packet parsePacket(String str){
        String[] args = str.split(",");
        if(args.length<3) return null;

        boolean myAuthTokenAvailable = true;
        int myAuthToken = 0;
        try {
            myAuthToken = Integer.parseInt(args[1]);
        }catch (NumberFormatException ne){
            myAuthTokenAvailable = false;
        }

        PacketType myPacketType = null;
        if(args[2].equals(PacketType.AUTHENTICATION_ERROR.toString())){
            myPacketType = PacketType.AUTHENTICATION_ERROR;
        }
        else if(args[2].equals(PacketType.AUTHENTICATION_SUCCESS.toString())){
            myPacketType = PacketType.AUTHENTICATION_SUCCESS;
        }
        else if(args[2].equals(PacketType.INFO_RES.toString())){
            myPacketType = PacketType.INFO_RES;
        }
        else if(args[2].equals(PacketType.PLAYING_RES.toString())){
            myPacketType = PacketType.PLAYING_RES;
        }
        else if(args[2].equals(PacketType.WATCH_RES.toString())){
            myPacketType = PacketType.WATCH_RES;
        }
        else if(args[2].equals(PacketType.SCORE_TABLE_RES.toString())){
            myPacketType = PacketType.SCORE_TABLE_RES;
        }

        String myBody = "";
        for(int i=3;i<args.length;i++){
            if(!args[i].equals("$")) {
                if(i!=3) myBody += ",";
                myBody += args[i];
            }
        }
        return new Packet(myPacketType,myBody,myAuthToken,myAuthTokenAvailable);
    }

    public int getAuthToken() {
        return authToken;
    }

    public boolean isAuthTokenAvailable() {
        return authTokenAvailable;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public String getBody() {
        return body;
    }
}

