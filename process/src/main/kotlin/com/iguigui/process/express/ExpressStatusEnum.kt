package com.iguigui.process.express

enum class ExpressStatusEnum(val status: Int, val desc: String) {


    PICKED_UP(1, "揽收"),
    IN_TRANSIT(2, "在途"),
    DELIVERY_IN_PROGRESS(3, "派送"),
    FAILED(4, "查询失败"),
    DELIVERED(5, "签收"),
    AT_CUSTOMS(8, "清关"),
    BUYER_REFUSED_TO_SIGN(14, "拒签");


}