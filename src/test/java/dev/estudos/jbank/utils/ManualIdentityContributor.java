package dev.estudos.jbank.utils;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.persister.internal.StandardPersisterClassResolver;
import org.hibernate.persister.spi.PersisterClassResolver;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.service.spi.ServiceContributor;

public class ManualIdentityContributor implements ServiceContributor {

	@Override
	public void contribute(StandardServiceRegistryBuilder serviceRegistryBuilder) {
		serviceRegistryBuilder.addService(PersisterClassResolver.class, new ManualIdentityPersisterClassResolver());
	}
	
	public static class ManualIdentityPersisterClassResolver extends StandardPersisterClassResolver {

		private static final long serialVersionUID = 1L;
		
		public Class<? extends EntityPersister> singleTableEntityPersister() {
			return ManualIdentityTableEntityPersister.class;
		}
		
	}
	
	public static class ManualIdentityTableEntityPersister extends SingleTableEntityPersister {

		public ManualIdentityTableEntityPersister(PersistentClass persistentClass, EntityDataAccess cacheAccessStrategy,
				NaturalIdDataAccess naturalIdRegionAccessStrategy, PersisterCreationContext creationContext)
				throws HibernateException {
			super(persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, creationContext);
		}
		
		public Serializable insert(Object[] fields, Object object, SharedSessionContractImplementor session)
				throws HibernateException {
			
			Serializable identifier = this.getIdentifier(object);
			
			if (hasIdentifierProperty() && identifier == null) {
				return super.insert(fields, object, session);
			} else {
				insert(identifier, fields, object, session);
				
				return identifier;
			}
		}
				
	}
	
}
