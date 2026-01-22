package com.yowyob.template.domain.model;

import java.util.UUID;

public class OrganisationBuilder {
    private UUID id;
    private String name;
    private Address address;
    private OrgType type;
    private UUID actorId;

    public enum OrgType {
        DRIVER, CLIENT
    }

    public OrganisationBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public OrganisationBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public OrganisationBuilder withAddress(Address address) {
        this.address = address;
        return this;
    }

    public OrganisationBuilder withActorId(UUID actorId) {
        this.actorId = actorId;
        return this;
    }

    public OrganisationBuilder asDriver() {
        this.type = OrgType.DRIVER;
        return this;
    }

    public OrganisationBuilder asClient() {
        this.type = OrgType.CLIENT;
        return this;
    }

    public Organisation build() {
        Organisation org;
        if (type == OrgType.DRIVER) {
            org = DriverOrganisation.builder()
                    .id(id)
                    .name(name)
                    .address(address)
                    .actorId(actorId)
                    .build();
        } else {
            org = ClientOrganisation.builder()
                    .id(id)
                    .name(name)
                    .address(address)
                    .actorId(actorId)
                    .build();
        }
        return org;
    }
}
