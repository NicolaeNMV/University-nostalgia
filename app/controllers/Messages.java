package controllers;

import java.util.ArrayList;
import java.util.List;

import play.Logger;
import play.data.validation.Required;
import play.data.validation.Validation;

import models.Message;
import models.MessageInfo;
import models.User;

public class Messages extends Secure {
    
  public static void list(String box) {
      List<Message> messages = null;
      User connected = connectedUser();
      if(box==null || box.equals("all")) {
          messages = Message.userInbox(connected.id);
          messages.addAll(Message.userSendbox(connected.id));
          flash.put("box", "all");
      }
      else if(box.equals("inbox")) {
          messages = Message.userInbox(connected.id);
          flash.put("box", "inbox");
      }
      else if(box.equals("sendbox")) {
          messages = Message.userSendbox(connected.id);
          flash.put("box", "sendbox");
      }
      else
          notFound();
      
      
      List<MessageInfo> msges = new ArrayList<MessageInfo>();
      for(Message m : messages)
          msges.add(new MessageInfo(m));
      render(msges);
  }
  
  public static void view(Long id) {
      User connected = connectedUser();
      Message message = Message.findById(id);
      if(!message.userConcerned(connected.id))
          forbidden();
      render(message);
  }
  
  public static void create(@Required Long receiver, @Required String content) {
      if (validation.hasErrors()) {
            validation.keep();
            params.flash();
            informError();
            list(flash.get("box"));
        }
        User user = User.findById(receiver);
        User connected = connectedUser();
        new Message(connected, user, content).save();
        informSuccess();
        list(flash.get("box"));
  }
  
  public static void delete(Long id) {
      User connected = connectedUser();
      Message message = Message.findById(id);
      if(!message.userConcerned(connected.id))
          forbidden();
      if(connected.id.equals(message.sender.id))
          message.deleteBySender = true;
      else if(connected.id.equals(message.receiver.id))
          message.deleteByReceiver = true;
      if(message.deleteBySender && message.deleteByReceiver)
        message.delete();
      else
        message.save();
      list(flash.get("box"));
  }
  
}
