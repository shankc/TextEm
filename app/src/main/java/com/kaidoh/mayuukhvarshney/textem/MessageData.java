package com.kaidoh.mayuukhvarshney.textem;

/**
 * Created by mayuukhvarshney on 23/04/16.
 */
public class MessageData {


        // Number from witch the sms was send
        private String number;
        // SMS text body
        private String body;

       private String ID;
    private Boolean fromUser;

    private  int date;

        public String getNumber() {
            return number;
        }
    public int getDate(){
        return date;
    }
        public void senttFromUser(boolean value){
            this.fromUser=value;

        }
        public void setNumber(String number) {
            this.number = number;
        }
    public void setDate(int date){
        this.date=date;
    }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    public void setID(String ID){
        this.ID=ID;
    }

       public String getID(){
           return ID;
       }
    public boolean getFrom(){
        return fromUser;
    }


}
