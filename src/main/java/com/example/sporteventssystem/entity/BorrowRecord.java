package com.example.sporteventssystem.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table("borrow_record")
public class BorrowRecord {

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String borrowNo;
    private Long userId;
    private Long equipmentId;
    private Integer quantity;
    private String status;
    private String applyReason;
    private LocalDateTime expectedReturn;
    private LocalDateTime actualReturn;
    private Long approverId;
    private LocalDateTime approveTime;
    private String approveRemark;
    private Integer overdueRemind;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
