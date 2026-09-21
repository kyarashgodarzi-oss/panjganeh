package com.panjganeh.game.billing

object BazaarConfig {
    const val SKU_VIP_MONTHLY = "challenge_arena_vip_monthly"
    const val SKU_VIP_YEARLY = "challenge_arena_vip_yearly"
    const val SKU_COINS_1000 = "challenge_arena_coins_1000"
    const val SKU_COINS_5000 = "challenge_arena_coins_5000"
    const val SKU_TICKETS_10 = "challenge_arena_tickets_10"

    data class ProductInfo(
        val sku: String,
        val titleFa: String,
        val descriptionFa: String,
        val priceFormatted: String,
        val iconType: String, // "VIP", "COIN", "TICKET"
        val badgeFa: String? = null
    )

    val ALL_PRODUCTS = listOf(
        ProductInfo(
            sku = SKU_VIP_MONTHLY,
            titleFa = "اشتراک VIP ماهانه",
            descriptionFa = "بدون تبلیغات + ۲ برابر پاداش نبردها + نشان VIP طلایی + آواتار اختصاصی",
            priceFormatted = "۳۹,۰۰۰ تومان",
            iconType = "VIP",
            badgeFa = "پرفروش"
        ),
        ProductInfo(
            sku = SKU_VIP_YEARLY,
            titleFa = "اشتراک VIP سالانه (تخفیف ویژه)",
            descriptionFa = "یک سال عضویت ویژه VIP + ۱۰ بلیط رایگان + ۵۰۰۰ سکه پاداش",
            priceFormatted = "۲۸۹,۰۰۰ تومان",
            iconType = "VIP",
            badgeFa = "۵۰٪ تخفیف"
        ),
        ProductInfo(
            sku = SKU_COINS_1000,
            titleFa = "بسته ۱۰۰۰ سکه طلا",
            descriptionFa = "مناسب خرید آواتارهای جدید و ارتقای مشخصات",
            priceFormatted = "۱۵,۰۰۰ تومان",
            iconType = "COIN"
        ),
        ProductInfo(
            sku = SKU_COINS_5000,
            titleFa = "صندوقچه ۵۰۰۰ سکه طلا",
            descriptionFa = "بهترین ارزش خرید سکه با هدیه ۱۰۰۰ سکه اضافی",
            priceFormatted = "۴۹,۰۰۰ تومان",
            iconType = "COIN",
            badgeFa = "ارزش خرید بالا"
        ),
        ProductInfo(
            sku = SKU_TICKETS_10,
            titleFa = "بسته ۱۰ بلیط ورودی نبرد",
            descriptionFa = "شرکت در رقابت‌های بزرگ پنجگانه و چالش‌های دشوار",
            priceFormatted = "۱۹,۰۰۰ تومان",
            iconType = "TICKET"
        )
    )
}
