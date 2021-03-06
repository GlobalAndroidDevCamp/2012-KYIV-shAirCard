package com.gtug.shaircard.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gtug.shaircard.model.EMFService;
import com.gtug.shaircard.model.Event;
import com.gtug.shaircard.model.VCard;
import com.gtug.shaircard.model.VCardImage;

public class GetVCardImage extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		String cardIdStr = req.getParameter("vcardId");
		String password = req.getParameter("password");
		long cardId;
		if (cardIdStr == null) {
			resp.getWriter().println("FAILURE: No vcard ID");
			return;
		} else {
			cardId = Long.parseLong(cardIdStr);
		}
		EntityManager em = EMFService.get().createEntityManager();
		Query qGetCard = em.createQuery("SELECT c FROM VCard c WHERE c.id = :id");
		qGetCard.setParameter("id", cardId);
		VCard card = (VCard)qGetCard.getSingleResult();
		
		Query qGetEvent = em.createQuery("SELECT e FROM Event e WHERE e.id = :id");
		qGetEvent.setParameter("id", card.getEventId());
		Event event = (Event)qGetEvent.getSingleResult();

		if (false && event.getPassword() != null
				&& !event.getPassword().equals("")
				&& !event.getPassword().equals(password)) {
			resp.getWriter().println("FAILURE: Wrong event password");
			em.close();
			return;
		}
		
		Query qGetImage = em.createQuery("SELECT i FROM VCardImage i WHERE i.vcardId = :id");
		qGetImage.setParameter("id", card.getId());
		VCardImage vci = (VCardImage)qGetImage.getSingleResult();
		
		if (vci.getImage() == null) {
			return;
		}
		
		resp.setContentType("image/jpeg");
		
		OutputStream osw = resp.getOutputStream();
		
		
		
		osw.write(vci.getImage().getBytes());
	}

}
