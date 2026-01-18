CREATE TABLE vip_tenant.tenants (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    application_url VARCHAR(255),
    plan VARCHAR(50) NOT NULL, -- Added plan as it is a required form field
    region VARCHAR(50) NOT NULL,
    status VARCHAR(50) DEFAULT 'active',
    health INTEGER DEFAULT 100,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
-- Indexes for common lookups
CREATE INDEX idx_tenants_name ON vip_tenant.tenants(name);
CREATE INDEX idx_tenants_region ON vip_tenant.tenants(region);