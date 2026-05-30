package com.hometown.shipping.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(schema = "shipping", name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private String partner;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String destPincode;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal charge;

    @Column(nullable = false)
    private Integer etaDays;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Shipment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getPartner() { return partner; }
    public void setPartner(String partner) { this.partner = partner; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDestPincode() { return destPincode; }
    public void setDestPincode(String destPincode) { this.destPincode = destPincode; }

    public BigDecimal getCharge() { return charge; }
    public void setCharge(BigDecimal charge) { this.charge = charge; }

    public Integer getEtaDays() { return etaDays; }
    public void setEtaDays(Integer etaDays) { this.etaDays = etaDays; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
