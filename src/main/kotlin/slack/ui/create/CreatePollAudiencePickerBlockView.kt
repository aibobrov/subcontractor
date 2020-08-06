package slack.ui.create

import com.slack.api.model.kotlin_extension.block.composition.dsl.OptionGroupObjectDsl
import com.slack.api.model.kotlin_extension.block.dsl.LayoutBlockDsl
import slack.model.SlackChannel
import slack.model.SlackUser
import slack.ui.base.SlackBlockUIRepresentable

class CreatePollAudiencePickerBlockView(
    private val users: List<SlackUser>,
    private val channels: List<SlackChannel>
) : SlackBlockUIRepresentable {
    override fun representIn(builder: LayoutBlockDsl) {
        buildAudienceSelect(builder)
    }

    private fun buildAudienceSelect(builder: LayoutBlockDsl) {
        builder.input {
            blockId(CreationConstants.BlockID.AUDIENCE)
            label(AUDIENCE_HEADER_LABEL)
            multiStaticSelect {
                actionId(CreationConstants.ActionID.POLL_AUDIENCE)
                placeholder(AUDIENCE_SELECT_PLACEHOLDER)
                optionGroups {
                    buildAudienceUserOptionGroup(this)
                    buildAudienceChannelsOptionGroup(this)
                }
            }
        }
    }

    private fun buildAudienceUserOptionGroup(builder: OptionGroupObjectDsl) {
        builder.optionGroup {
            label(USER_GROUP_NAME)
            options {
                users.forEach {
                    this.option {
                        plainText(it.name)
                        value(it.id)
                    }
                }
            }
        }
    }

    private fun buildAudienceChannelsOptionGroup(builder: OptionGroupObjectDsl) {
        builder.optionGroup {
            label(CHANNEL_GROUP_NAME)
            options {
                channels.forEach {
                    this.option {
                        plainText(it.name)
                        value(it.id)
                    }
                }
            }
        }
    }

    companion object {
        private const val USER_GROUP_NAME = "User"
        private const val CHANNEL_GROUP_NAME = "Channel"
        private const val AUDIENCE_HEADER_LABEL = "Audience"
        private const val AUDIENCE_SELECT_PLACEHOLDER = "Select audience"
    }

}
