package tschuba.ez.booth.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repositories for {@link EntityModel}.
 */
public class Repositories {
    private Repositories() {
    }

    /**
     * Repository for {@link EntityModel.Booth}.
     */
    @Repository
    public interface Booth extends JpaRepository<EntityModel.Booth, EntityModel.Booth.Key> {
    }

    /**
     * Repository for {@link EntityModel.Vendor}.
     */
    @Repository
    public interface Vendor extends JpaRepository<EntityModel.Vendor, EntityModel.Vendor.Key> {
    }

    /**
     * Repository for {@link EntityModel.PurchaseItem}.
     */
    @Repository
    public interface PurchaseItem extends JpaRepository<EntityModel.PurchaseItem, EntityModel.PurchaseItem.Key> {
    }

    /**
     * Repository for {@link EntityModel.Purchase}.
     */
    @Repository
    public interface Purchase extends JpaRepository<EntityModel.Purchase, EntityModel.Purchase.Key> {
    }
}
