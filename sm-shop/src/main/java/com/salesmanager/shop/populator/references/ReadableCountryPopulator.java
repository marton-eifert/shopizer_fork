package com.salesmanager.shop.populator.references;

import org.apache.commons.collections4.CollectionUtils;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.model.reference.zone.ZoneDescription;
import com.salesmanager.shop.model.references.ReadableCountry;
import com.salesmanager.shop.model.references.ReadableZone;

public class ReadableCountryPopulator extends AbstractDataPopulator<Country, ReadableCountry> {

	@Override
	public ReadableCountry populate(Country source, ReadableCountry target, MerchantStore store, Language language)
			throws ConversionException {
		
		if(target==null) {
			target = new ReadableCountry();
		}
		
		target.setId(new Long(source.getId()));




/**********************************
 * CAST-Finding START #1 (2024-02-06 09:25:45.031225):
 * TITLE: Avoid primitive type wrapper instantiation
 * DESCRIPTION: Literal values are built at compil time, and their value stored directly in the variable. Literal strings also benefit from an internal mechanism of string pool, to prevent useless duplication, according to the fact that literal string are immutable. On the contrary, values created through wrapper type instantiation need systematically the creation of a new object with many attributes and a life process to manage, and can lead to redondancies for identical values.
 * STATUS: OPEN
 * CAST-Finding END #1
 **********************************/


		target.setCode(source.getIsoCode());
		target.setSupported(source.getSupported());
		if(!CollectionUtils.isEmpty(source.getDescriptions())) {
			target.setName(source.getDescriptions().iterator().next().getName());
	    }
		
		if(!CollectionUtils.isEmpty(source.getZones())) {
			for(Zone z : source.getZones()) {




/**********************************
 * CAST-Finding START #2 (2024-02-06 09:25:45.031225):
 * TITLE: Avoid instantiations inside loops
 * DESCRIPTION: Object instantiation uses memory allocation, that is a greedy operation. Doing an instantiation at each iteration could really hamper the performances and increase resource usage.  If the instantiated object is local to the loop, there is absolutely no need to instantiate it at each iteration : create it once outside the loop, and just change its value at each iteration. If the object is immutable, create if possible a mutable class. If the aim is to create a consolidated data structure, then, unless the need is to release the data case by case, it could be better to make a single global allocation outside the loop, and fill it with data inside the loop.
 * STATUS: OPEN
 * CAST-Finding END #2
 **********************************/


				ReadableZone readableZone = new ReadableZone();
				readableZone.setCountryCode(target.getCode());
				readableZone.setId(z.getId());
				if(!CollectionUtils.isEmpty(z.getDescriptions())) {




/**********************************
 * CAST-Finding START #3 (2024-02-06 09:25:45.031225):
 * TITLE: Avoid nested loops
 * DESCRIPTION: This rule finds all loops containing nested loops.  Nested loops can be replaced by redesigning data with hashmap, or in some contexts, by using specialized high level API...  With hashmap: The literature abounds with documentation to reduce complexity of nested loops by using hashmap.  The principle is the following : having two sets of data, and two nested loops iterating over them. The complexity of a such algorithm is O(n^2). We can replace that by this process : - create an intermediate hashmap summarizing the non-null interaction between elements of both data set. This is a O(n) operation. - execute a loop over one of the data set, inside which the hash indexation to interact with the other data set is used. This is a O(n) operation.  two O(n) algorithms chained are always more efficient than a single O(n^2) algorithm.  Note : if the interaction between the two data sets is a full matrice, the optimization will not work because the O(n^2) complexity will be transferred in the hashmap creation. But it is not the main situation.  Didactic example in Perl technology: both functions do the same job. But the one using hashmap is the most efficient.  my $a = 10000; my $b = 10000;  sub withNestedLoops() {     my $i=0;     my $res;     while ($i < $a) {         print STDERR "$i\n";         my $j=0;         while ($j < $b) {             if ($i==$j) {                 $res = $i*$j;             }             $j++;         }         $i++;     } }  sub withHashmap() {     my %hash = ();          my $j=0;     while ($j < $b) {         $hash{$j} = $i*$i;         $j++;     }          my $i = 0;     while ($i < $a) {         print STDERR "$i\n";         $res = $hash{i};         $i++;     } } # takes ~6 seconds withNestedLoops();  # takes ~1 seconds withHashmap();
 * STATUS: OPEN
 * CAST-Finding END #3
 **********************************/


					for(ZoneDescription d : z.getDescriptions()) {
						if(d.getLanguage().getId() == language.getId()) {
							readableZone.setName(d.getName());
							continue;
						}
					}
				}
				target.getZones().add(readableZone);
			}
		}
		
		return target;
	}

	@Override
	protected ReadableCountry createTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
