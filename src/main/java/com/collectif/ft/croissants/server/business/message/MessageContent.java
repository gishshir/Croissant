package com.collectif.ft.croissants.server.business.message;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import com.collectif.ft.croissants.server.business.message.MessageBlock.AlignBlock;
import com.collectif.ft.croissants.server.business.message.MessageBlock.MessageType;
import com.collectif.ft.croissants.server.service.message.MessageManager;


/**
 * Ensemble de block pour la constitution d'un email multipart
 * <br>Trois types de blocks:
 * <ul>
 *   <li>html : texte sous forme html
 *   <li>image : image gif ou png à insérer
 *   <li>sautLine : saut de ligne entre les blocks
 * @author sylvie
 *
 */
public class MessageContent {
	
	
	private static final String BEGIN_HTML = "<html><body style=\"background-color:#F6E8B1;\">";
	private static final String END_HTML = "</body></html>";
	
	private static final String BEGIN_TABLE = "<table>";
	private static final String END_TABLE = "</table>";
	
	private static final String BEGIN_ROW = "<tr valign=\"middle\">";
	private static final String END_ROW = "</tr>";
	
	private static final String BEGIN_CELL = "<td>";
	private static final String END_CELL = "</td>";

	private static final String PREFIXE_IMG = "img_";
	
	private static final MessageBlock SAUT_BLOCK = new MessageBlock(MessageType.sautLigne, null);
	
	private final List<MessageBlock> _listBlock = new ArrayList<MessageBlock>();
	// Map [cid - imageName]
	private final Map<String, String> _mapCidForImageName = new LinkedHashMap<String, String>();
	
	public void addMessageBlock(final MessageBlock messageBlock) {
		if (messageBlock != null) {
		  this._listBlock.add(messageBlock);
		}
	}
	public void addSautBlock() {
		this._listBlock.add(SAUT_BLOCK);
	}
	public Multipart getMultipart() throws MessagingException {

	     // use a MimeMultipart as we need to handle the file attachments
		 Multipart multipart = new MimeMultipart("related");
		  
	      //========================================
	     // first part...
	        // add the message body to the mime message
	     //=======================================     
	     multipart.addBodyPart( this.buildContentBodyPart() );
	     
	      //========================================
	      // other parts (the images itself)
	      //========================================
	      for (String cid: this._mapCidForImageName.keySet()) {
	    	final String imgName = this._mapCidForImageName.get(cid);
			multipart.addBodyPart(this.buildImgBodyPart(cid, imgName));
		  }
		  
		  return multipart;
		  
	}
	private String getBeginCell(int colSpan, AlignBlock alignBlock) {
		
		StringBuilder cell = new StringBuilder("<td");
		if (colSpan > 1) {
			cell.append(" colspan=\"" + colSpan + "\"");
		}
		if (alignBlock != null) {
			
			
			switch (alignBlock) {
			
			case left: cell.append(" align=\"left\"");
				break;

			case center: cell.append(" align=\"center\"");
				break;
				
			case right:  cell.append(" align=\"right\"");
				break;
			}
		}
		
		cell.append(" />");
		
		return cell.toString();
	}
	private BodyPart buildContentBodyPart() throws MessagingException {
		
		  this._mapCidForImageName.clear();
		  int imgCount = 0;
		 
		  StringBuilder body = new StringBuilder();
		  body.append(BEGIN_HTML);
		  body.append(BEGIN_TABLE);
		  body.append(BEGIN_ROW);
		 
		  // for each block
		  for (MessageBlock messageBlock : this._listBlock) {
			
			  if (messageBlock.getMessageType() == MessageType.sautLigne) {
				  body.append(END_ROW);
				  body.append(BEGIN_ROW);
			  }
			  else {
				 int colspan = messageBlock.getColSpan();
				 AlignBlock alignBlock = messageBlock.getAlignBlock();
				 body.append((colspan == 1 && alignBlock == null)?BEGIN_CELL:this.getBeginCell(colspan, alignBlock));
				  
				 switch (messageBlock.getMessageType()) {
				    
				 case lien:
				   case html: body.append(messageBlock.getHtmlValue(null));
					    break;

				    case image: imgCount++;
				        String cid = PREFIXE_IMG + imgCount;
				        body.append(messageBlock.getHtmlValue(cid));
				        this._mapCidForImageName.put(cid, messageBlock.getValue());
					    break;
				}
				 
				 body.append(END_CELL);
			  }
		  }
		  
		  body.append(END_ROW);
		  body.append(END_TABLE);
		  body.append(END_HTML);
		  
	      MimeBodyPart bodypart = new MimeBodyPart();
	      bodypart.setHeader("Content-Type","text/html; charset=\"utf-8\"");
	      bodypart.setContent( body.toString(), "text/html; charset=utf-8" );
	      bodypart.setHeader("Content-Transfer-Encoding", "quoted-printable"); 
	      
	      return bodypart;
		
	}
	
	
	private BodyPart buildImgBodyPart ( String cid, String imgName) throws MessagingException {
		
        BodyPart imageBodyPart = new MimeBodyPart();
        DataSource fds = new FileDataSource
          (new File(MessageManager.getInstance().getImageRootPath(), imgName));
        imageBodyPart.setDataHandler(new DataHandler(fds));
        imageBodyPart.setHeader("Content-ID",cid);
        
        return imageBodyPart;
		
	}
	
	//----------------------------------- overriding Object
	@Override
	public String toString() {
		if (this._listBlock == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (MessageBlock messageBlock : this._listBlock) {
			sb.append(messageBlock.toString());
		}
		
		return sb.toString();
	}
	
	
	
}
