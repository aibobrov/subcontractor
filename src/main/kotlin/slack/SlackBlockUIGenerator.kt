package slack

import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.kotlin_extension.block.withBlocks
import core.UIGenerator

// Dummy implementation
class SlackBlockUIGenerator : UIGenerator<List<LayoutBlock>> {
    override fun generate(): List<LayoutBlock> {
        return withBlocks {
        }
    }
}
