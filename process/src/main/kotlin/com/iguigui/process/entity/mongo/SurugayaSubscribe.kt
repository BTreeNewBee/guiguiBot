package com.iguigui.process.entity.mongo

import com.iguigui.process.dto.mercari.Item
import org.springframework.data.annotation.Id

//群ID-用户ID-关键字
data class SurugayaSubscribe(
    @Id
    val id: String?,
    val groupId: Long,
    val subscribe: ArrayList<SurugayaSubscribeInfo>)

data class SurugayaSubscribeInfo(val userId: Long, val keyword: String, val condition: String, val priceRange: String)



//爬下来的需要存储的信息
data class SurugayaSpiderContent(
    @Id
    val id: String?,
    val key: String,
    var items: List<SurugayaItem>)

//<div class="item_detail">
//								<p class="condition">
//									クラシックCD
//								</p>
//								<p class="title"><a href="/product-other/230000075">本田美奈子 / アメイジング・グレイス</a></p>
//								<p class="brand">コロムビアミュージックエンタテインメント</p>
//								<p class="release_date">発売日：2005/10/19</p>
//							</div>
//							<div class="item_price">
//								<p class="price">品切れ</p>
//								<p class="price_teika">定価：￥2,200</p>
//								<div class="mgnT10 highlight-box">
//									<p><strong>こちらからもご購入いただけます</strong></p>
//									<p class="mgnB5 mgnT5">
//										<span class="icon_mp_brown mgnR5">マケプレ</span>
//										<span class="text-red fontS15"><strong>￥260</strong></span>
//									</p>
//									<p class="mgnL-3"><a href="/product-other/230000075"
//											class="text-blue-light">(3点の中古品)</a></p>
//								</div>
//							</div>
data class SurugayaItem(val condition: String,
                        val title: String,
                        val releaseDate: String,
                        val price: String,
                        val priceTeika: String,
                        val priceMp: String,
                        val priceMpNum: String,
                        val url: String)