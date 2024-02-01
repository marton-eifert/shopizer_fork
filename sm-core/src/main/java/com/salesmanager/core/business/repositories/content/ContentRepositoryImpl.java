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
 * CAST-Finding START #1 (2024-02-01 21:19:54.506965):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * OUTLINE: The code line `List<Content> contents = q.getResultList();` is most likely affected. - Reasoning: It retrieves a list of `Content` objects from the query result, which is used in the subsequent loop. - Proposed solution: No solution proposed.  The code line `List<ContentDescription> descriptions = new ArrayList<ContentDescription>();` is most likely affected. - Reasoning: It initializes an empty list to store `ContentDescription` objects, which are created and added in the subsequent loop. - Proposed solution: No solution proposed.  The code line `for(Content c : contents) {` is most likely affected. - Reasoning: It iterates over the `contents` list, accessing the `Content` objects. - Proposed solution: No solution proposed.  The code line `String name = c.getDescription().getName();` is most likely affected. - Reasoning: It accesses the `name` property of the `Description` object associated with each `Content` object. - Proposed solution: No solution proposed.  The code line `String url = c.getDescription().getSeUrl();` is most likely affected. - Reasoning: It accesses the `seUrl` property of the `Description` object associated with each `Content` object. - Proposed solution: No solution proposed.  The code line `ContentDescription contentDescription = new ContentDescription();` is most likely affected. - Reasoning: It creates a new `ContentDescription` object for each `Content` object in the loop. - Proposed solution: Move the instantiation of `ContentDescription` outside the loop and reuse the same object for each iteration.  The code line `contentDescription.setName(name);` is most likely affected. - Reasoning: It sets the `name` property of the `ContentDescription` object. - Proposed solution: No solution proposed.  The code line `contentDescription.setSeUrl(url);` is most likely affected. - Reasoning: It sets the `se
 * INSTRUCTION: {instruction}
 * STATUS: IN_PROGRESS
 * CAST-Finding END #1
 **********************************/


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