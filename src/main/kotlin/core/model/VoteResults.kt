package core.model

import core.model.base.OptionID

data class VoteResults(val results: Map<OptionID, List<VoterInfo>>) : Map<OptionID, List<VoterInfo>> by results
