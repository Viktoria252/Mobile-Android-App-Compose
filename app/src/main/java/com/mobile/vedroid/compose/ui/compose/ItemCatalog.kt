package com.mobile.vedroid.compose.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobile.vedroid.compose.R
import com.mobile.vedroid.compose.ui.theme.RentCamTheme

//Модель данных
data class CatalogItem(
    val id: Int,
    val name: String,
    val category: String,
    val description: String,
    val pricePerDay: Int,
    val status: ItemStatus,
    val imageRes: Int? = null // если null — рисуем иконку категории
)

enum class ItemStatus {
    AVAILABLE,   // Доступно
    RENTED,      // Занято
    BOOKED       // Забронировано
}

/**
 * Цвет и текст для статуса — визуальное оформление разных категорий статуса.
 */
private fun statusColor(status: ItemStatus): Color = when (status) {
    ItemStatus.AVAILABLE -> Color(0xFF4CAF50) // зелёный
    ItemStatus.RENTED    -> Color(0xFFF44336) // красный
    ItemStatus.BOOKED    -> Color(0xFFFF9800) // оранжевый
}

private fun statusLabel(status: ItemStatus): String = when (status) {
    ItemStatus.AVAILABLE -> "Доступно"
    ItemStatus.RENTED    -> "Занято"
    ItemStatus.BOOKED    -> "Забронировано"
}

/**
 * Иконка для категории — визуальное оформление разных типов оборудования.
 */
private fun categoryIcon(category: String): ImageVector = when (category.lowercase()) {
    "камера", "camera"        -> Icons.Default.PhotoCamera
    "видеокамера", "video"    -> Icons.Default.Videocam
    "свет", "light"           -> Icons.Default.Lightbulb
    "звук", "audio", "mic"    -> Icons.Default.Mic
    else                       -> Icons.Default.Category
}

/**
 * Основная карточка товара.
 */
@Composable
fun ItemCatalog(
    item: CatalogItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //  Изображение / иконка категории
            ItemImage(item = item)

            Spacer(modifier = Modifier.width(12.dp))

            // Текстовое описание
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Категория (мелким текстом над названием)
                Text(
                    text = item.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                // Название
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Краткое описание
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Цена + статус
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Цена — цифры
                    Text(
                        text = "${item.pricePerDay} ₽/день",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Статус — цветной бейдж
                    StatusBadge(status = item.status)
                }
            }
        }
    }
}

/**
 * Изображение товара или fallback-иконка категории.
 */
@Composable
private fun ItemImage(item: CatalogItem) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        if (item.imageRes != null) {
            Image(
                painter = painterResource(item.imageRes),
                contentDescription = item.name,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Иконка категории вместо картинки
            Icon(
                imageVector = categoryIcon(item.category),
                contentDescription = item.category,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Цветной бейдж статуса (доступно/занято/забронировано).
 */
@Composable
private fun StatusBadge(status: ItemStatus) {
    val color = statusColor(status)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                color = color,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = statusLabel(status),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

// Preview для проверки карточки

@Preview(showBackground = true, name = "Доступно")
@Composable
private fun ItemCatalogPreviewAvailable() {
    RentCamTheme {
        ItemCatalog(
            item = CatalogItem(
                id = 1,
                name = "Sony A7 III",
                category = "Камера",
                description = "Полнокадровая беззеркальная камера с объективом 28-70mm f/3.5-5.6",
                pricePerDay = 2500,
                status = ItemStatus.AVAILABLE
            )
        )
    }
}

@Preview(showBackground = true, name = "Занято")
@Composable
private fun ItemCatalogPreviewRented() {
    RentCamTheme {
        ItemCatalog(
            item = CatalogItem(
                id = 2,
                name = "Canon EF 50mm f/1.8 STM",
                category = "Объектив",
                description = "Светосильный фикс-объектив, идеален для портретной съёмки",
                pricePerDay = 500,
                status = ItemStatus.RENTED
            )
        )
    }
}

@Preview(showBackground = true, name = "Забронировано")
@Composable
private fun ItemCatalogPreviewBooked() {
    RentCamTheme {
        ItemCatalog(
            item = CatalogItem(
                id = 3,
                name = "Manfrotto MT055XPRO3",
                category = "Штатив",
                description = "Алюминиевый штатив с центральной колонкой и быстросъёмной площадкой",
                pricePerDay = 700,
                status = ItemStatus.BOOKED
            )
        )
    }
}