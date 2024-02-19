package com.hss01248.baseapiresponse;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BaseApiResponse<T>  {

    public T data;
    public boolean success;
    public String errCode;
    public String errMsg;
    public int sysTime = (int) System.currentTimeMillis();


    public final static String CODE_CANCEL = "cancel";
    public final static String CODE_TIMEOUT = "time out";
    public final static String CODE_JAVA_EXCEPTION = "exception";
    public final static String CODE_ILLIGAL_PARAMS = "illeagal params";

    public final static String CODE_EMPTY = "empty";
    public final static String CODE_UNLOGIN = "unLogin";


    public  Map<String,Object> asMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("success",success);
        map.put("errCode",errCode);
        map.put("errMsg",errMsg);
        if( data instanceof Map || data ==  null){
            map.put("data",data);
        }else  if(data instanceof String){
            map.put("data",data);
        }else  if(data instanceof Integer || data instanceof Long
                || data instanceof Float || data instanceof Double
                || data instanceof Boolean || data instanceof Character
                || data instanceof Byte){
            map.put("data",data);
        } else {
            map.put("data",objToMap(data));
        }

        return  map;
    }

    public static BaseApiResponse fromMap(Map<String, Object> data){
        BaseApiResponse response = new BaseApiResponse();
        try {
            response.success = (boolean) data.get("success");
            response.errCode = (String) data.get("errCode");
            response.errMsg = (String) data.get("errMsg");
        }catch (Throwable throwable){
            response.success = false;
            response.errCode = throwable.getClass().getSimpleName();
            response.errMsg = throwable.getMessage();
        }
        response.data = data;
        return response;
    }

    //Object转Map
    public static Map<String, Object> objToMap(Object obj)  {
        Map<String, Object> map = new HashMap<String, Object>();
        Class<?> cla = obj.getClass();
        //不能获取到父类上的
        Field[] fields = cla.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String keyName = field.getName();
            Object value = null;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
/*            if (value == null)
                value = "";*/
            map.put(keyName, value);
        }
        return map;
    }


    public static <T> BaseApiResponse success(T data){
        BaseApiResponse response = new BaseApiResponse();
        response.success = true;
        response.data = data;
        return response;
    }


    public static BaseApiResponse success(Map<String, Object> data){
        BaseApiResponse response = new BaseApiResponse();
        response.success = true;
        response.data = data;
        return response;
    }



    public static Builder put(String key,Object value){

        return BaseApiResponse.newBuilder().put(key,value);
    }

    public static  Builder  success(){
        return new Builder();
    }

    public static BaseApiResponse error(String code, String msg){
        BaseApiResponse response = new BaseApiResponse();
        response.success = false;
        response.errCode = code;
        response.errMsg = msg;
        return response;
    }

    @Override
    public String toString() {
        return "MyResponse{" +
                "data=" + data +
                ", success=" + success +
                ", errCode='" + errCode + '\'' +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }

    public static BaseApiResponse cancel(){
        return error(CODE_CANCEL,"");
    }

    public static BaseApiResponse empty(){
        return error(CODE_EMPTY,"data is empty");
    }
    public static BaseApiResponse unlogin(){
        return error(CODE_UNLOGIN,"need login to continue");
    }

    public static BaseApiResponse timeout(int time){
        return error(CODE_TIMEOUT,time+"");
    }
    public static BaseApiResponse exception(Throwable throwable){
        if(throwable.getCause() != null){
            throwable = throwable.getCause();
        }
        return error(CODE_JAVA_EXCEPTION,throwable.getClass().getSimpleName()+": "+throwable.getMessage());
    }
    public static BaseApiResponse illeagalParms(String params){
        return error(CODE_ILLIGAL_PARAMS,params);
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private Map<String, Object> data = new HashMap();

        private Builder() {
        }

        public Builder put(String key,Object value) {
            data .put(key, value);
            return this;
        }

        public BaseApiResponse build() {
            return BaseApiResponse.success(data);
        }
    }
}
