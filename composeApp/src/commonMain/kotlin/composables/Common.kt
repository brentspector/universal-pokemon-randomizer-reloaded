package composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import viewModels.ModViewModel

@Composable
fun <T> RadioButtonGroup(categoryName: String, mod: ModViewModel<T>) {
    Column(Modifier.selectableGroup()) {
        Text(categoryName)
        mod.getEnumValues().forEach {
            Row {
                RadioButton(
                    selected = mod.isSelected(it),
                    onClick = { mod.setState(it) },
                    modifier = Modifier.semantics { contentDescription = it.toString() }
                )
                Text(it.toString())
            }
        }
    }
}
