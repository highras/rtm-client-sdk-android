~~~ java

   //relogin start callback function  this willbe call relgion starting
    //reloginWillStart param userid answer-relogin result  reloginCount-relogin times
    public interface  IReloginStart{
        boolean reloginWillStart(long userid, RTMAnswer answer, int reloginCount);
    }

    //relogin final result if successful == false mean relogin final failed
    public interface  IReloginCompleted{
        void   reloginCompleted(long uid, boolean successful, RTMAnswer answer, int reloginCount);
    }

    public interface IRTMEmptyCallback {
        void onResult(RTMAnswer answer);
    }

    
    public interface IRTMCallback<T> {
        void onResult(T t, RTMAnswer answer);
    }

    interface DoubleStringCallback{
        void onResult(String str1, String str2, int errorCode);
    }
    

public class RTMStruct {

    public enum TranslateType {
        Chat,
        Mail
    }

    // Sensitive word  filte type
    public enum ProfanityType {
        Off, //no  filter
        Censor //if have Sensitive word the result will be replace '*'
    }

    public enum FileMessageType
    {
        IMAGEFILE(40),  //pic
        AUDIOFILE(41),  //audio
        VIDEOFILE(42),  //video
        NORMALFILE(50); //noamal
        private int value;

        FileMessageType(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    //if successful errorCode is 0 other else have mistake ;the errorMsg has detail info
    public static class RTMAnswer
    {
        public int errorCode = -1;
        public String errorMsg = "";
        RTMAnswer(){};
        RTMAnswer(int _code, String msg){
            errorCode = _code;
            errorMsg = msg;
        }
        public String getErrInfo(){
            return  " " + errorCode + "-" + errorMsg;
        }
    }

    //
    public static class Unread extends RTMAnswer
    {
        public List<Long> p2pList; //uids
        public List<Long> groupList;//group ids
    }

    public static class MessageType
    {
        public static final byte WITHDRAW = 1;
        public static final byte GEO = 2;
        public static final byte MULTILOGIN = 7; //mutil lgoin
        public static final byte CHAT = 30; //chat
        public static final byte CMD = 32;//cmd
        public static final byte REALAUDIO = 35;//real time voice
        public static final byte REALVIDEO = 36;//real time video
        public static final byte IMAGEFILE = 40;//pic
        public static final byte AUDIOFILE = 41;//audio file
        public static final byte VIDEOFILE = 42;//video file
        public static final byte NORMALFILE = 50;//normal file
    }

    //serverpush 
    public static class RTMMessage
    {
        public long fromUid;    //send uid
        public long toId;       //dest id according to messagetype maybe userid/groupid/roomid
        public byte messageType;  //message type you can custom messagetype 51-127
        public long messageId;  //
        public String stringMessage; //
        public byte[] binaryMessage; //
        public String attrs;        //additional message 
        public long modifiedTime;   //server return time
        public FileStruct fileInfo; //file struct
        public TranslatedInfo translatedInfo = null; //chat struct
    }

    public static class HistoryMessage extends RTMMessage //
    {
        public long cursorId;       //messaget cursor id
    }

    //
    public static class SingleMessage extends RTMAnswer{
        public long cusorId; //messaget cursor id
        public byte messageType;  ////message type you can custom messagetype 51-127
        public String stringMessage; 
        public byte[] binaryMessage;
        public String attrs;    //additional message 
        public long modifiedTime;   //server return time
        public FileStruct fileInfo; //file struct
    }

    public static class TranslatedInfo extends RTMAnswer//聊天消息结构
    {
        public String source; //source language
        public String target; //dest language
        public String sourceText; //source text
        public String targetText; //dest text
    }

    public static class CheckResult extends RTMAnswer{ //text/voice/image/video
        public int result = -1;      //check result 0-pass 2-fail
        public String text;     //(only text check)result text
        public List<String> wlist; //(only text check)Sensitive word list
        public List<Integer> tags; //Sensitive word classification
    }

    public static class GroupInfoStruct extends RTMAnswer{
        public String  publicInfo; //group/room public info
        public String  privateInfo; //group/room private info
    }


    public static class AudioTextStruct extends RTMAnswer{
        public String  text; //audio trans text result
        public String  lang;
    }


    public static class ModifyTimeStruct extends RTMAnswer{
        public long  modifyTime; //server return time
        public long  messageId = 0; 
    }

    //
    public static class HistoryMessageResult extends RTMAnswer{
        public int count;   //messaget count
        public long lastId; //last messaget cursor id
        public long beginMsec; //begin time(millisecond)
        public long endMsec;    //end time(millisecond)
        public List<HistoryMessage> messages;
    }

    public static class MembersStruct extends RTMAnswer{
        public  HashSet<Long> uids;
    }

    public static class DataInfo extends RTMAnswer{
        public String info;
    }

    public static class AttrsStruct extends RTMAnswer{
        public List<Map<String, String>> attrs;
        //  the map has some default key
        //  ce：connetction's endpoint，if kickout this endpoint you can use kickout function
        //  login：login time
        //  my：current connection's attrs
    }

    public static class PublicInfo extends RTMAnswer{
        public Map<String, String> publicInfos; //group/room/user's public info
    }

    public static class FileStruct{
        public String url;     //file's url
        public String surl;    //thumbnail url if file is picture this value will be set
        public long fileSize;  //byte
        public boolean isRTMaudio = false; //is rtm audio
        public String lang;    //voice language if isRTMaudio ==true this value will be set
        public int duration;   //voice duration if isRTMaudio ==true this value will be set
        public String codec;   //voice codec if isRTMaudio ==true this value will be set
        public int srate;       //SAMPLE RATE if isRTMaudio ==true this value will be set
    }
}

~~~
