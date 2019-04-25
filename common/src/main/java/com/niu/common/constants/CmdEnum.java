package com.niu.common.constants;

/**
 * command
 */
public enum CmdEnum {
   LOGIN(1000,"登录"),LOGOUT(1100,"登出"),HEARTBEAT(1200,"心跳"),SEND(1300,"发消息");
   private int code;
   private String msg;

   CmdEnum(int code, String msg) {
      this.code = code;
      this.msg = msg;
   }

   public static boolean checkCmd(int cmdCode){
      for (CmdEnum cmd:CmdEnum.values()) {
         if(cmd.code==cmdCode){
            return true;
         }
      }
      return false;
   }

   public static CmdEnum getCmdBycode(int cmdCode) {
      for (CmdEnum cmd : CmdEnum.values()) {
         if (cmd.code == cmdCode) {
            return cmd;
         }
      }
      return null;
   }

   public int getCode() {
      return code;
   }

   public void setCode(int code) {
      this.code = code;
   }

   public String getMsg() {
      return msg;
   }

   public void setMsg(String msg) {
      this.msg = msg;
   }
}
