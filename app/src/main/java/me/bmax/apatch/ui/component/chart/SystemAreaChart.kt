package me.bmax.apatch.ui.component.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries

@Composable
fun SystemAreaChart(
    title: String,
    dataPoints: List<Float>,
    unit: String = "%",
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val colors = MaterialTheme.colorScheme
    val currentValue = dataPoints.lastOrNull() ?: 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
                color = colors.surface,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Text(
            text = "${currentValue.toInt()}$unit",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = color
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 12.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                color = colors.onSurfaceVariant,
                fontSize = 12.sp
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 12.dp)
        )

        if (dataPoints.isNotEmpty()) {
            val modelProducer = remember { CartesianChartModelProducer.build() }

            LaunchedEffect(dataPoints) {
                modelProducer.runTransaction {
                    lineSeries {
                        series(dataPoints)
                    }
                }
            }

            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        listOf(
                            rememberLineSpec(
                                shader = DynamicShaders.color(color),
                                backgroundShader = DynamicShaders.verticalGradient(
                                    colors = arrayOf(
                                        color.copy(alpha = 0.3f),
                                        color.copy(alpha = 0.0f)
                                    )
                                )
                            )
                        )
                    )
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 36.dp),
            )
        } else {
            Text(
                text = "--",
                style = MaterialTheme.typography.displaySmall.copy(
                    color = colors.outline.copy(alpha = 0.3f)
                ),
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
    }
}
