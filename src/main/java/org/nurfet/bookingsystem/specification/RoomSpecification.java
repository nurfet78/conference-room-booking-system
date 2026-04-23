package org.nurfet.bookingsystem.specification;
import org.nurfet.bookingsystem.dto.spec.RoomFilter;
import org.nurfet.bookingsystem.entity.Room;
import org.springframework.data.jpa.domain.Specification;

public class RoomSpecification {

    public static Specification<Room> nameLike(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return cb.conjunction();

            return cb.like(cb.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Room> capacityAtLeast(Integer capacity) {
        return (root, query, cb) -> {
            if (capacity == null) return cb.conjunction();

            return cb.greaterThanOrEqualTo(root.get("capacity"), capacity);
        };
    }

    public static Specification<Room> descriptionLike(String description) {
        return (root, query, cb) -> {
            if (description == null || description.isBlank()) return cb.conjunction();

            return cb.like(cb.lower(root.get("description")),
                    "%" + description.toLowerCase() + "%");
        };
    }

    public static Specification<Room> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) return cb.conjunction();

            return cb.equal(root.get("active"), active);
        };
    }

    public static Specification<Room> fromFilter(RoomFilter filter) {
        return Specification
                .where(nameLike(filter.name()))
                .and(capacityAtLeast(filter.capacity()))
                .and(descriptionLike(filter.description()))
                .and(isActive(filter.active()));
    }
}