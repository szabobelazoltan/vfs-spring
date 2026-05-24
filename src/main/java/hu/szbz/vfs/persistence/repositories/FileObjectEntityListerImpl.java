package hu.szbz.vfs.persistence.repositories;

import hu.szbz.vfs.persistence.model.*;
import hu.szbz.vfs.persistence.parameters.FileObjectFilter;
import hu.szbz.vfs.persistence.parameters.FileObjectSort;
import hu.szbz.vfs.persistence.result.FileObjectResultItem;
import hu.szbz.vfs.persistence.result.ResultPage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public class FileObjectEntityListerImpl implements FileObjectEntityLister {
    private final EntityManager entityManager;

    public FileObjectEntityListerImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public ResultPage<FileObjectResultItem> listByFilters(ActorEntity actor, int pageSize, int pageIndex, Collection<FileObjectFilter<?>> filters, Collection<FileObjectSort> sort) {
        long totalMatches = countMatches(actor, filters);
        List<FileObjectResultItem> items = collectResultForPage(actor, pageSize, pageIndex, filters, sort);
        return new ResultPage<>(items, Long.valueOf(totalMatches).intValue(), pageSize, pageIndex);
    }

    private long countMatches(ActorEntity actor, Collection<FileObjectFilter<?>> filters) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Long.class);
        var root = cq.from(AccessEntity.class);
        var foPath = root.get(AccessEntity_.fileObject);
        var conditions = createConditions(cb, root, foPath, actor, filters);
        return entityManager.createQuery(cq
                .select(cb.count(foPath)).where(conditions))
                .getSingleResult();
    }

    private List<FileObjectResultItem> collectResultForPage(
            ActorEntity actor,
            int pageSize,
            int pageIndex,
            Collection<FileObjectFilter<?>> filters,
            Collection<FileObjectSort> sort) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Object[].class);
        var root = cq.from(AccessEntity.class);
        var foPath = root.get(AccessEntity_.fileObject);
        var conditions = createConditions(cb, root, foPath, actor, filters);
        var orders = createOrders(cb, foPath, sort);
        var selection = cb.array(
                foPath.get(FileObjectEntity_.id),
                foPath.get(FileObjectEntity_.externalId),
                foPath.get(FileObjectEntity_.type),
                foPath.get(FileObjectEntity_.creationDateTime),
                foPath.get(FileObjectEntity_.modificationDateTime),
                foPath.get(FileObjectEntity_.status),
                foPath.get(FileObjectEntity_.name),
                foPath.get(FileObjectEntity_.deletionDateTime),
                foPath.get(FileObjectEntity_.contentReference),
                root.get(AccessEntity_.permissions)
        );
        return entityManager.createQuery(cq
                        .orderBy(orders)
                        .where(conditions)
                        .select(selection))
                .setMaxResults(pageSize)
                .setFirstResult((pageIndex - 1) * pageSize)
                .getResultList()
                .stream()
                .map(this::mapResultItem)
                .toList();
    }

    private Predicate createConditions(
            CriteriaBuilder cb,
            Root<AccessEntity> root,
            Path<FileObjectEntity> fileObjectEntityPath,
            ActorEntity actor,
            Collection<FileObjectFilter<?>> filters) {
        Predicate actorCondition = cb.equal(root.get(AccessEntity_.actor), actor);
        Predicate[] conditions = new Predicate[filters.size() + 1];
        int i = 0;
        for (FileObjectFilter<?> filter : filters) {
            conditions[i++] = filter.createPredicate(cb, fileObjectEntityPath);
        }
        conditions[i] = actorCondition;
        return cb.and(conditions);
    }

    private Order[] createOrders(
            CriteriaBuilder cb,
            Path<FileObjectEntity> fileObjectEntityPath,
            Collection<FileObjectSort> sorts
    ) {
        Order[] orders = new Order[sorts.size()];
        int i = 0;
        for (FileObjectSort sort : sorts) {
            orders[i++] = sort.createOrder(cb, fileObjectEntityPath);
        }
        return orders;
    }

    private FileObjectResultItem mapResultItem(Object[] t) {
        long id = (long) t[0];
        String externalId = (String) t[1];
        FileObjectType type = (FileObjectType) t[2];
        LocalDateTime creationDateTime = (LocalDateTime) t[3];
        LocalDateTime modificationDateTime = (LocalDateTime) t[4];
        FileObjectStatus status = (FileObjectStatus) t[5];
        String name = (String) t[6];
        LocalDateTime deletionDateTime = (LocalDateTime) t[7];
        String contentReference = (String) t[7];
        int permissions = (int) t[8];
        return new FileObjectResultItem(
                id,
                externalId,
                type,
                creationDateTime,
                modificationDateTime,
                status,
                name,
                deletionDateTime,
                contentReference,
                permissions
        );
    }
}
