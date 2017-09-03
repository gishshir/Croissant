package com.collectif.ft.croissants.server.service.message;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.collectif.ft.croissants.server.business.Alert;
import com.collectif.ft.croissants.server.business.Alert.AlertType;
import com.collectif.ft.croissants.server.business.message.Message;
import com.collectif.ft.croissants.server.business.message.Message.MessageState;
import com.collectif.ft.croissants.server.business.message.MessageBlock;
import com.collectif.ft.croissants.server.business.message.MessageBlock.MessageType;
import com.collectif.ft.croissants.server.business.message.MessageContent;


/**
 * Ajoute les messages dans MessageBoxOutput 
 * <br/>
 * Parcours MessageBoxOutput et exp�die les message par ordre de priorit�.
 * G�re les reprises sur erreurs
 * @author sylvie
 *
 */
public class MessageManager {
	
	private static final Log log = LogFactory.getLog(MessageManager.class);
	
	private static final DateFormat DATE_FORMAT = SimpleDateFormat.getDateInstance();
	
	public static final String IMAGES_DIRECTORY = "/images/";
	private static final String EMAIL_LOGO = "smileyCafe.gif";
	private static final String IMG_DELETE = "poubelle.gif";
	private static final String IMG_CHANGE_DATE = "calendrier.png";
	private static final String IMG_TODO = "smalllogocafe.gif";
	
	private static final ResourceBundle resoureBundle  = ResourceBundle.getBundle("messages", Locale.getDefault());
	
	private static final String CONTENT_NO_DATE = "content.nodate";
	
	private static final String SUBJECT_CHANGE_DATE = "subject.change.date";
	private static final String CONTENT_CHANGE_DATE = "content.change.date";
	private static final String SUBJECT_DELETE_USER = "subject.delete.user";
	private static final String CONTENT_DELETE_USER = "content.delete.user";
	private static final String SUBJECT_TASK_TODO = "subject.task.todo";
	private static final String CONTENT_TASK_TODO = "content.task.todo";
	private static final String SUBJECT_TASK_INCOMPLETE = "subject.incomplete.task";
	private static final String CONTENT_TASK_INCOMPLETE_USER_FREE = "content.incomplete.task.userfree";
	private static final String CONTENT_TASK_INCOMPLETE_USER_NOFREE = "content.incomplete.task.userNoFree";


	private static final MessageManager instance = new MessageManager();
	public static final MessageManager getInstance() {
		return instance;
	}
	
	private boolean _initDone = false;
	
	private Session _session = null;
	private Properties _sessionProps = null;
	private SmtpParams _smtpParams;
	private Transport _transport = null;
	private Timer _timerSendMessage;
	private boolean _stoppingTimer = false;
	private boolean _activeSendMail = false;
	private String _imageRootPath;
	
	private String _applicationUrl = null;
	private MessageBlock _footerMessageBlock = null;
	
	//private URL _moduleRootUrl;
	
	private static final MessagePattern _patternChangeDate = new MessagePattern(SUBJECT_CHANGE_DATE, CONTENT_CHANGE_DATE);
	private static final MessagePattern _patternDeleteUser = new MessagePattern(SUBJECT_DELETE_USER, CONTENT_DELETE_USER);
	private static final MessagePattern _patternTaskToDo = new MessagePattern(SUBJECT_TASK_TODO, CONTENT_TASK_TODO);
	private static final MessagePattern _patternIncompleteTaskUserFree = new MessagePattern(SUBJECT_TASK_INCOMPLETE, CONTENT_TASK_INCOMPLETE_USER_FREE);
	private static final MessagePattern _patternIncompleteTaskUserNoFree = new MessagePattern(SUBJECT_TASK_INCOMPLETE, CONTENT_TASK_INCOMPLETE_USER_NOFREE);
	
	private final MessageBoxOutput _messageBoxOutput = new MessageBoxOutput();

	//------------------------------------ private constructor
	private MessageManager() {}

	//-------------------------------- overriding object
	@Override
	protected void finalize() throws Throwable {
		if (_transport != null) {
			_transport.close();
		}
		_session = null;
		super.finalize();
	}

	//------------------------------------ public methods
	public String getImageRootPath() {
		return this._imageRootPath;
	}
	public void reinit() {
		
		if (this._activeSendMail) {
			
			  // init javamail session
			  this._session = Session.getDefaultInstance(this._sessionProps);
			  this._session.setDebug(true);			
		}
		this._transport = null;
	}
	public void setApplicationUrl(String applicationUrl) {
		this._applicationUrl = applicationUrl;
	}
	public void init(SmtpParams smtpParams,  String moduleRootPath) {
		
		try {
					
			if(!this._initDone) {
			this._activeSendMail = smtpParams._activeSendMail;
			this._imageRootPath = moduleRootPath + IMAGES_DIRECTORY;
			
			// FIXME use of mail.smtp.auth = true ??
			this._sessionProps = new Properties();
			this._sessionProps.setProperty("mail.transport.protocol", smtpParams._mailSmtpProtocol);
			this._sessionProps.setProperty("mail.smtp.host", smtpParams._mailSmtpHost);
			this._sessionProps.setProperty("mail.smtp.user", smtpParams._mailSmtpUser);
			this._sessionProps.setProperty("mail.from", smtpParams._mailSmtpFrom);
			
			this._smtpParams = smtpParams;
			
		    // launch timer
		    this.loadTimer();
            // init session
		    this.reinit();
		    
		    this._initDone = true;
			}
			
		} catch (Exception e) {
			log.error(e.toString());
			throw new RuntimeException("Error in MessageManager.init()");
		}
		
	}
	

	public void createMessageTaskToDo(Alert alert, Date taskDate, int delai) {
		
		log.info("createMessageTaskToDo()");
		
		 MessageContent messageContent = new MessageContent();
		 
    	 messageContent.addMessageBlock(this.getLogoMessageBlock());
    	 messageContent.addSautBlock();
    	 
		 messageContent.addMessageBlock(new MessageBlock(MessageType.image, IMG_TODO));
		 String content = _patternTaskToDo.getContent(DATE_FORMAT.format(taskDate), "" + delai);
		 messageContent.addMessageBlock(new MessageBlock(MessageType.html, content));

		 String subject = _patternTaskToDo.getSubject("");
    	 this.createAndAddMessage(alert, subject, messageContent);
	}
	public void createMessageIncompleteTask(Alert alert, Date taskDate, boolean userfree) {
		
		log.info("createMessageIncompleteTask()");
		
		 MessageContent messageContent = new MessageContent();
		 
    	 messageContent.addMessageBlock(this.getLogoMessageBlock());
    	 messageContent.addSautBlock();
    	 
		 messageContent.addMessageBlock(new MessageBlock(MessageType.image, IMG_TODO, 1));
		 String content = (userfree)? _patternIncompleteTaskUserFree.getContent(DATE_FORMAT.format(taskDate)):
			 _patternIncompleteTaskUserNoFree.getContent(DATE_FORMAT.format(taskDate));
		 messageContent.addMessageBlock(new MessageBlock(MessageType.html, content, 1));
		 String subject = _patternIncompleteTaskUserFree.getSubject("");
   	     this.createAndAddMessage(alert, subject, messageContent);
	}
    public void createMessageDeleteUser(Alert alert) {
    	
    	log.info("createMessageDeleteUser()");

    	 MessageContent messageContent = new MessageContent();
    	 
    	 messageContent.addMessageBlock(this.getLogoMessageBlock());
    	 messageContent.addSautBlock();
    	 
		 messageContent.addMessageBlock(new MessageBlock(MessageType.image, IMG_DELETE, 1));
    	 String content = _patternDeleteUser.getContent(alert.getUser().getLogin());
    	 
    	 MessageBlock messageBlock1 = new MessageBlock(MessageType.html, content);
    	 messageBlock1.setStyle("color:orangered; font-weight:bold;");	 
    	 messageContent.addMessageBlock(messageBlock1);
    	 
    	 messageContent.addSautBlock();
    	 
    	 MessageBlock messageBlock2 = new MessageBlock(MessageType.html,
    			 "(Si c'est une erreur, contacter l'administrateur du site.)", 2);
    	 messageBlock2.setStyle("color:black; font-size:small;");	 
    	 messageContent.addMessageBlock(messageBlock2);
    	 
    	 String subject = _patternDeleteUser.getSubject("");
    	 this.createAndAddMessage(alert, subject, messageContent);
	}
    public void createMessageChangeDate(Alert alert, Date oldDate, Date newDate) {
    	
    	log.info("createMessageChangeDate()");

    	 MessageContent messageContent = new MessageContent();
    	 
    	 messageContent.addMessageBlock(this.getLogoMessageBlock());
    	 messageContent.addSautBlock();
    	 
		 messageContent.addMessageBlock(new MessageBlock(MessageType.image, IMG_CHANGE_DATE));
    	 String content = _patternChangeDate.getContent( ((oldDate == null)?
             resoureBundle.getString(CONTENT_NO_DATE):DATE_FORMAT.format(oldDate)), 
    			 ((newDate == null)? resoureBundle.getString(CONTENT_NO_DATE):DATE_FORMAT.format(newDate)));
    	 messageContent.addMessageBlock(new MessageBlock(MessageType.html, content, 1));
    	 
    	 messageContent.addSautBlock();
    	 messageContent.addMessageBlock(this.getFooterMessageBlock());
    	 
    	 
    	 String subject = _patternChangeDate.getSubject("");
    	 this.createAndAddMessage(alert, subject, messageContent);
	}
	
    private MessageBlock getLogoMessageBlock() {
    	
   	 MessageBlock logo = new MessageBlock(MessageType.image, EMAIL_LOGO, 2);
   	 logo.alignCenter();
   	 return logo;
    }
    
    private MessageBlock getFooterMessageBlock() {
    	
    	if (this._footerMessageBlock == null && this._applicationUrl != null)
    	{
 
    		this._footerMessageBlock = new MessageBlock(MessageType.lien, this._applicationUrl);
    	}
    	return  this._footerMessageBlock;
    }
	//------------------------------------ private methods
	private void loadTimer() {
		
		log.info("loadTimer()");
		
		// manage send messages --------------------
		
		if (!this.cancelTimer())
		{
		  //le timer n'existe pas on le crée
		  this._timerSendMessage = new Timer();
		}

		TimerTask timerTask = this.createTimeTask();
		this._stoppingTimer = false;
		this._timerSendMessage.schedule(timerTask, 10);		

	}
	
	private TimerTask createTimeTask() {
		
		log.info("createTimeTask()");
		return new TimerTask() {
			
			@Override
			public void run() {
				
				while(true) {
					
				  //get next message and sent it
					if (_stoppingTimer) {
						log.warn("stopping timer...");
						break;
					}
					else  {
					  try {
						  manageSendNextMessage();
					} catch (Throwable e) {
						log.error("Error in manageSendMessages() ... stopping timer...");
                        break;
					}
				     
				  }
				}
				
				log.warn("...timer stopped.");
			}
		};
	}
	
	/**
	 * 
	 * @return true si existe un timer à arreter
	 */
	public boolean cancelTimer() {
		if (this._timerSendMessage != null) {
			log.info("cancelTimer()");
			this._stoppingTimer = true;
			
			// pour debloquer le timer
			 synchronized (this._messageBoxOutput) {
			    this._messageBoxOutput.notify();
			}
			  
			return true;
		} else {
			return false;
		}
	}



    private void manageSendNextMessage()  {

    	log.info("manageSendNextMessage() ...");
    	
    	Message messageToSend = null;
    	synchronized (this._messageBoxOutput) {
			
		
		  while(this._messageBoxOutput.isEmpty()) {
			try {
				log.info("waiting...");
				this._messageBoxOutput.wait();
				
				// le timer doit s'arreter
				if (this._stoppingTimer) {
					log.info("...cancel timer");
					this._timerSendMessage.cancel();
					return;
				}
				log.info("...end waiting");
			} catch (InterruptedException e) {
				return;
			}
		   }
		// get next message and send it.
        messageToSend = this._messageBoxOutput.depileNextMessage();
        //  use mail API
        log.info("message to send: " + ((messageToSend == null)?"":messageToSend.toString()));
        
    	}
        
    	// envoi reel du mail
    	boolean isError = false;
        if (this._activeSendMail && messageToSend != null) {
        	
		  try {
			  
			if (this._transport == null) {
				  // create javamail transport
				  this._transport = _session.getTransport(this._smtpParams._mailSmtpProtocol);
			}
			  
			
        MimeMessage mimeMessage = this.buildMimeMessage(messageToSend);
		   
		 this._transport.connect(this._smtpParams._mailSmtpUser, this._smtpParams._mailSmtpPwd);	
		 this._transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
		
		  messageToSend.setState(MessageState.send);
		
		  } catch (Exception e) {
			log.error(e.toString());
			// TODO gerer un retry ou remettre le message dans la pile
			messageToSend.setState(MessageState.error);
			isError = true;
		  }
		  finally {
			
			  // Dans tous les cas on ferme la connection après utilisation
			  try {
					this._transport.close();
				} catch (MessagingException ignored) {
                  log.warn(ignored.toString());
				}
			 
				 if (isError) {
					// reinitialisation sera necessaire
					this.reinit();
				 }
		  }
        	
        }
    }

	 private void createAndAddMessage(Alert alert, String subject, MessageContent content) {
	    	
	    AlertType alertType = alert.getAlerteType();
	    final Message message = new Message(alert.getUser().getEmail().getEmail(), subject, content, alertType.getPriority(), alertType.getLevel());
	    log.debug("addMessage() - user: " + alert.getUser().getLogin() + " message: " + message.toString());
	       

	    synchronized (this._messageBoxOutput) {
			
	    	this._messageBoxOutput.addMessage(message);
	    	this._messageBoxOutput.notify();
		}
	  }
	 
	    
	    private MimeMessage buildMimeMessage(Message messageToSend) throws Exception {
	    	
	    	InternetAddress internetAddress;
			try {
				internetAddress = new InternetAddress(messageToSend.getEmail());
			} catch (AddressException e) {
				log.error("Error in new InternetAddress()");
				throw e;
			}
	    	
			  MimeMessage mimeMessage = new MimeMessage(_session);
			  try {
				mimeMessage.setFrom();
				  mimeMessage.addRecipient(RecipientType.TO, internetAddress);
				  mimeMessage.setSubject(messageToSend.getSubject());
				  
				  Multipart multipart = messageToSend.getContent().getMultipart();
				  mimeMessage.setContent( multipart ); 
				  

			} catch (MessagingException e) {
				log.error("Error in new MimeMessage()");
				throw e;
			}
	  
			  return mimeMessage;
	    }
	 

	 
	 //======================================================= INNER class =================
	  
	 
	 public static class SmtpParams {

		 private final String _mailSmtpProtocol = "smtp";
		 private final String _mailSmtpHost;
		 private final String _mailSmtpUser;
		 private final String _mailSmtpPwd;
		 private final String _mailSmtpFrom;
		 private final boolean _activeSendMail;
		 
		 public SmtpParams(final String mailSmtpHost, final String mailSmtpUser,
				 final String mailSmtpPwd, final String mailSmtpFrom, final boolean activeSendMail) {
			 this._mailSmtpHost = mailSmtpHost;
			 this._mailSmtpUser = mailSmtpUser;
			 this._mailSmtpPwd = mailSmtpPwd;
			 this._mailSmtpFrom = mailSmtpFrom;
			 this._activeSendMail = activeSendMail;
		 }
		 
	 }
	 
		//==================================== MessagePattern
		public static class MessagePattern {

			
			private final MessageFormat _messageFormatSubject;
			private final MessageFormat _messageFormatContent;
			
			public String getSubject(String ... args) {
				return this._messageFormatSubject.format(args);
			}
			public String getContent(String ... args) {
				return this._messageFormatContent.format(args);
			}
			
			public MessagePattern (String keySubject, String keyContent) {
				this._messageFormatSubject = new MessageFormat(resoureBundle.getString(keySubject));
				this._messageFormatContent = new MessageFormat(resoureBundle.getString(keyContent));
			}
		}
}
