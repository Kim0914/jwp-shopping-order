package cart.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order {
    private Long id;
    private Member member;
    private List<OrderItem> orderItems;
    private Point spendPoint;
    private LocalDateTime createdAt;

    public Order(Long id, Member member, List<OrderItem> orderItems, Point spendPoint, LocalDateTime createdAt) {
        this.id = id;
        this.member = member;
        this.orderItems = orderItems;
        this.spendPoint = spendPoint;
        this.createdAt = createdAt;
    }

    public String getThumbnailUrl() {
        return orderItems.stream()
                .map(OrderItem::getProduct)
                .map(Product::getImageUrl)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new); // TODO
    }

    public Price calculateTotalPrice() {
        return orderItems.stream()
                .map(OrderItem::getPrice)
                .reduce(Price::plus)
                .orElseThrow(IllegalArgumentException::new); // TODO
    }

    public Price calculateSpendPrice() {
        Price totalPrice = calculateTotalPrice();
        return totalPrice.minus(Price.from(spendPoint.getAmount()));
    }

    public Point calculateRewardPoint(double percent) {
        Price spendPrice = calculateSpendPrice();
        long amount = spendPrice.getAmount();
        double reward = amount * (percent / 100);
        return new Point((long) Math.ceil(reward));
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    public Point getSpendPoint() {
        return spendPoint;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(getId(), order.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
