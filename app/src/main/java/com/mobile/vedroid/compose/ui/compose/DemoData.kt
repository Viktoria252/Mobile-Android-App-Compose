package com.mobile.vedroid.compose.ui.compose

import com.mobile.vedroid.compose.R
import com.mobile.vedroid.compose.network.ItemRequest

fun demoCatalog(page: Int = 1): List<ItemRequest> {
    val base = listOf(
        ItemRequest("1", "Sony A7 III", "Камера",
            "Полнокадровая беззеркальная камера 24 МП", 2500.00,
            null),
        ItemRequest("2", "Canon EOS R6", "Камера",
            "Полнокадровая камера с 4K-видео и стабилизацией", 3200.00,
            null),
        ItemRequest("3", "Nikon Z6 II", "Камера",
            "Гибридная камера для фото и видео 24 МП", 2800.00,
            null),
        ItemRequest("4", "Sony FX3", "Видеокамера",
            "Кинематографическая камера с S-Cinetone", 5500.00,
            null),
        ItemRequest("5", "Blackmagic Pocket 6K", "Видеокамера",
            "Компактная кинокамера с сенсором Super 35", 4200.00,
            null),
        ItemRequest("6", "Canon EF 50mm f/1.8", "Объектив",
            "Светосильный фикс для портретной съёмки", 500.00,
            null),
        ItemRequest("7", "Sigma 24-70mm f/2.8", "Объектив",
            "Универсальный зум для репортажа", 1200.00,
            null),
        ItemRequest("8", "Sony FE 70-200mm f/4", "Объектив",
            "Телезум для спорта и wildlife", 1500.00,
            null),
        ItemRequest("9", "Aputure 120D II", "Свет",
            "LED-моноблок 180 Вт с Bowens-байонетом", 1800.00,
            null),
        ItemRequest("10", "Godox SL-60W", "Свет",
            "Компактный LED-источник 60 Вт", 700.00,
            null),
        ItemRequest("11", "Manfrotto MT055XPRO3", "Штатив",
            "Алюминиевый штатив до 9 кг", 700.00,
            null),
        ItemRequest("12", "DJI RS 3 Pro", "Стабилизатор",
            "Профессиональный стабилизатор до 4.5 кг", 3000.00,
            null),
    )

    return if (page == 1) base
    else base.mapIndexed { index, item ->
        item.copy(
            id = item.id + (page - 1) * 100 + index,
            name = "${item.name} (стр. $page)"
        )
    }
}