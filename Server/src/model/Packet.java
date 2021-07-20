package model;

public class Packet {
    private int clientID;
    private int requestID;
    private int authToken;
    private boolean authTokenAvailable;
    private PacketType packetType;
    private String body;

    public Packet(PacketType packetType, String body, int authToken, boolean authTokenAvailable, int clientID, int requestID) {
        this.packetType = packetType;
        this.body = body;
        this.authToken = authToken;
        this.authTokenAvailable = authTokenAvailable;
        this.requestID = requestID;
        this.clientID = clientID;

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

    public static Packet parsePacket(String str, int clientID){
        String[] args = str.split(",");
        if(args.length<3) return null;

        int myRequestID = Integer.parseInt(args[0]);
        boolean myAuthTokenAvailable = true;
        int myAuthToken = 0;
        try {
            myAuthToken = Integer.parseInt(args[1]);
        }catch (NumberFormatException ne){
            myAuthTokenAvailable = false;
        }
        PacketType myPacketType = null;
        if(args[2].equals(PacketType.AUTHENTICATION_REQ.toString())){
            myPacketType = PacketType.AUTHENTICATION_REQ;
        }
        else if(args[2].equals(PacketType.INFO_REQ.toString())){
            myPacketType = PacketType.INFO_REQ;
        }
        else if(args[2].equals(PacketType.BYE.toString())){
            myPacketType = PacketType.BYE;
        }
        else if(args[2].equals(PacketType.PLAYING_REQ.toString())){
            myPacketType = PacketType.PLAYING_REQ;
        }
        else if(args[2].equals(PacketType.WATCH_REQ.toString())){
            myPacketType = PacketType.WATCH_REQ;
        }
        else if(args[2].equals(PacketType.SCORE_TABLE_REQ.toString())){
            myPacketType = PacketType.SCORE_TABLE_REQ;
        }

        String myBody = "";
        for(int i=3;i<args.length;i++){
            if(!args[i].equals("$")) {
                if(i!=3) myBody += ",";
                myBody += args[i];
            }
        }
        return new Packet(myPacketType,myBody,myAuthToken,myAuthTokenAvailable,clientID,myRequestID);
    }

    public int getRequestID() {
        return requestID;
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

    public int getClientID() {
        return clientID;
    }
}

