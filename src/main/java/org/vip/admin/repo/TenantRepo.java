package org.vip.admin.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.vip.admin.model.onboard.Tenant;

@Repository
public interface TenantRepo extends CrudRepository<Tenant, String> {
}
