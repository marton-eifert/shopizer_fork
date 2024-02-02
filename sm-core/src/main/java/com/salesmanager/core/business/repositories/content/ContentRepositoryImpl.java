package com.salesmanager.core.business.repositories.content;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.salesmanager.core.model.content.Content;
import com.salesmanager.core.model.content.ContentDescription;
import com.salesmanager.core.model.content.ContentType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;


public class ContentRepositoryImpl implements ContentRepositoryCustom {

	
    @PersistenceContext
    private EntityManager em;
    
	@Override
	public List<ContentDescription> listNameByType(List<ContentType> contentType, MerchantStore store, Language language) {
		


			StringBuilder qs = new StringBuilder();

			qs.append("select c from Content c ");
			qs.append("left join fetch c.descriptions cd join fetch c.merchantStore cm ");
			qs.append("where c.contentType in (:ct) ");
			qs.append("and cm.id =:cm ");
			qs.append("and cd.language.id =:cl ");
			qs.append("and c.visible=true ");
			qs.append("order by c.sortOrder");

			String hql = qs.toString();
			Query q = this.em.createQuery(hql);

	    	q.setParameter("ct", contentType);
	    	q.setParameter("cm", store.getId());
	    	q.setParameter("cl", language.getId());
	

			@SuppressWarnings("unchecked")
			List<Content> contents = q.getResultList();
			
			List<ContentDescription> descriptions = new ArrayList<ContentDescription>();
			for(Content c : contents) {
				String name = c.getDescription().getName();
				String url = c.getDescription().getSeUrl();

				/**********************************
				 * CAST-Finding START #1 (2024-02-02 12:30:45.754910):
				 * TITLE: Avoid instantiations inside loops
				 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
				 * STATUS: WITHDRAWN
				 * CAST-Finding END #1
				 **********************************/
				
				// Instantiation inside loop is valid here
				ContentDescription contentDescription = new ContentDescription();
				contentDescription.setName(name);
				contentDescription.setSeUrl(url);
				contentDescription.setContent(c);
				descriptions.add(contentDescription);
					
			}
			
			return descriptions;

	}
	
	@Override
	public ContentDescription getBySeUrl(MerchantStore store,String seUrl) {

			StringBuilder qs = new StringBuilder();

			qs.append("select c from Content c ");
			qs.append("left join fetch c.descriptions cd join fetch c.merchantStore cm ");
			qs.append("where cm.id =:cm ");
			qs.append("and c.visible =true ");
			qs.append("and cd.seUrl =:se ");


			String hql = qs.toString();
			Query q = this.em.createQuery(hql);

	    	q.setParameter("cm", store.getId());
	    	q.setParameter("se", seUrl);
	

	    	Content content = (Content)q.getSingleResult();
			

			if(content!=null) {
					return content.getDescription();
			}
			
			@SuppressWarnings("unchecked")
			List<Content> results = q.getResultList();
	        if (results.isEmpty()) {
	        	return null;
	        } else if (results.size() >= 1) {
	        		content = results.get(0);
	        }
	        
			if(content!=null) {
				return content.getDescription();
			}
	        
			
			return null;

	}
    

}
