package com.mobile.vedroid.compose.ui.compose

import com.mobile.vedroid.compose.R

fun demoCatalog(page: Int = 1): List<CatalogItem> {
    val base = listOf(
        CatalogItem(1, "Sony A7 III", "Камера",
            "Полнокадровая беззеркальная камера 24 МП", 2500,
            imageRes = R.drawable.sony_a7),
        CatalogItem(2, "Canon EOS R6", "Камера",
            "Полнокадровая камера с 4K-видео и стабилизацией", 3200,
            imageRes = R.drawable.canon_eos_r6),
        CatalogItem(3, "Nikon Z6 II", "Камера",
            "Гибридная камера для фото и видео 24 МП", 2800,
            imageRes = R.drawable.nikon_z6),
        CatalogItem(4, "Sony FX3", "Видеокамера",
            "Кинематографическая камера с S-Cinetone", 5500,
            imageRes = R.drawable.sony_fx3),
        CatalogItem(5, "Blackmagic Pocket 6K", "Видеокамера",
            "Компактная кинокамера с сенсором Super 35", 4200,
            imageRes = R.drawable.blackmagic_pocket_6k),
        CatalogItem(6, "Canon EF 50mm f/1.8", "Объектив",
            "Светосильный фикс для портретной съёмки", 500,
            imageRes = R.drawable.canon_ef_50mm),
        CatalogItem(7, "Sigma 24-70mm f/2.8", "Объектив",
            "Универсальный зум для репортажа", 1200,
            imageRes = R.drawable.sigma_24_70mm),
        CatalogItem(8, "Sony FE 70-200mm f/4", "Объектив",
            "Телезум для спорта и wildlife", 1500,
            imageRes = R.drawable.sony_fe_70_200mm),
        CatalogItem(9, "Aputure 120D II", "Свет",
            "LED-моноблок 180 Вт с Bowens-байонетом", 1800,
            imageRes = R.drawable.aputure_120d),
        CatalogItem(10, "Godox SL-60W", "Свет",
            "Компактный LED-источник 60 Вт", 700,
            imageRes = R.drawable.godox_sl_60w),
        CatalogItem(11, "Manfrotto MT055XPRO3", "Штатив",
            "Алюминиевый штатив до 9 кг", 700,
            imageRes = R.drawable.manfrotto_mt055xpxo3),
        CatalogItem(12, "DJI RS 3 Pro", "Стабилизатор",
            "Профессиональный стабилизатор до 4.5 кг", 3000,
            imageRes = R.drawable.dji_rs_3_pro),
    )

    return if (page == 1) base
    else base.mapIndexed { index, item ->
        item.copy(
            id = item.id + (page - 1) * 100 + index,
            name = "${item.name} (стр. $page)"
        )
    }
}