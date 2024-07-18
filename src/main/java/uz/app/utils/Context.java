package uz.app.utils;


import uz.app.entity.User;

public class Context {
   static public User currentUser;

   public static void setCurrentUser(User user){
       currentUser = user;
   }

   public static User getCurrentUser(){
       return currentUser;
   }

}
