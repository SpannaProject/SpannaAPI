import org.spanna.Spanna;
 
public class VersionHandler {
 
    public static boolean is1_2_5(){
        if(Spanna.getVersion().contains("1.2.5")){
            return true;
        }
        return false;
    }
    public static boolean is1_3_2(){
        if(Spanna.getVersion().contains("1.3.2")){
            return true;
        }
        return false;
    }
    public static boolean is1_4_7(){
        if(Spanna.getVersion().contains("1.4.7")){
            return true;
        }
        return false;
    }
    public static boolean is1_5_2(){
        if(Spanna.getVersion().contains("1.5.2")){
            return true;
        }
        return false;
    }
    public static boolean is1_6_2(){
        if(Spanna.getVersion().contains("1.6.2")){
            return true;
        }
        return false;
    }
    public static boolean is1_6_4(){
        if(Spanna.getVersion().contains("1.6.4")){
            return true;
        }
        return false;
    }
 
    public static boolean is1_7_2(){
        if(Spanna.getVersion().contains("1.7") && Spanna.getServer().getClass().getPackage().getName().contains("R1")){
            return true;
        }
        return false;
    }
 
    public static boolean is1_7_9(){
        if(Spanna.getVersion().contains("1.7") && Spanna.getServer().getClass().getPackage().getName().contains("R3")){
            return true;
        }
        return false;
    }
    public static boolean is1_8(){
        if(Spanna.getVersion().contains("1.8")){
            return true;
        }
        return false;
    }
    public static boolean is1_8_1(){
        if(Spanna.getVersion().contains("1.8.1")){
            return true;
        }
        return false;
    }
 
    public static boolean matchesVersion(String s){
        if(Spanna.getVersion().contains(s) || Spanna.getServer().getClass().getPackage().getName().contains(s)){
            return true;
        }
        return false;
    }
}