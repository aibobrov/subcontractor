package core.model.storage


import core.model.PollCreationTime
import core.model.PollSerializer
import core.model.PollVoter
import core.model.base.DelegationRule
import core.model.base.DelegationRuleID
import core.model.base.Poll
import core.model.base.PollID
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.util.PSQLException


object Polls : Table() {
    val polId = varchar("pollId", 100)
    val poll = text("poll")

    override val primaryKey = PrimaryKey(polId)
}

object Times : Table() {
    val pollId = varchar("pollId", 100)
    val voterId = varchar("voterId", 100)
    val time = text("time")

    override val primaryKey = PrimaryKey(pollId, voterId)
}

object Delegations : Table() {
    val voterId = varchar("voterId", 100)
    val delegationRuleId = varchar("delegationRuleId", 100)
    val delegationRule = text("delegationRules")

    override val primaryKey = PrimaryKey(voterId, delegationRuleId)
}

class PollInfoStorageSqlImpl(
    url: String,
    driver: String,
    user: String,
    password: String
) : PollInfoStorage {

    private val pollSerializer = PollSerializer()
    private val database = Database.connect(url, driver, user, password)

    init {
        transaction(database) {
            SchemaUtils.create(Polls, Times, Delegations)
        }
    }

    override fun putPoll(pollId: PollID, poll: Poll) {
        transaction(database) {
            Polls.insert {
                it[polId] = pollId
                it[Polls.poll] = pollSerializer.toJson(poll)
            }
        }
    }

    override fun getPoll(pollId: PollID): Poll? {
        return transaction(database) {
            val pollJson = Polls.select {
               Polls.polId eq pollId
            }.map {
                it[Polls.poll]
            }
            if (pollJson.isEmpty()) {
                return@transaction null
            } else {
                pollSerializer.fromJson(pollJson[0])
            }
        }
    }

    override fun addDelegationRule(rule: DelegationRule) {
        transaction(database) {
            Delegations.insert {
                it[voterId] = rule.owner
                it[delegationRuleId] = rule.id
                it[delegationRule] = Json.encodeToString(rule)
            }
        }
    }

    override fun deleteDelegationRule(voter: PollVoter, ruleId: DelegationRuleID) {
        transaction(database) {
            Delegations.deleteWhere {
                (Delegations.voterId eq voter.id) and (Delegations.delegationRuleId eq ruleId)
            }
        }
    }

    override fun clearDelegationRules(voter: PollVoter) {
        transaction(database) {
            Delegations.deleteWhere { Delegations.voterId eq voter.id }
        }
    }

    override fun getDelegationRules(voter: PollVoter): List<DelegationRule> {
        return transaction(database) {
            val rules = mutableListOf<DelegationRule>()
            val rulesJson = Delegations.select {
                Delegations.voterId eq voter.id
            }.map {
                it[Delegations.delegationRule]
            }
            for (rule in rulesJson) {
                rules.add(Json.decodeFromString(rule))
            }
            rules
        }
    }

    override fun putPollCreationTimes(pollId: PollID, times: MutableMap<PollVoter, PollCreationTime>) {
        transaction(database) {
            for (entry in times) {
                val voter = entry.key
                val time = entry.value
                try {
                    Times.insert {
                        it[Times.pollId] = pollId
                        it[voterId] = voter.id
                        it[Times.time] = Json.encodeToString(time)
                    }
                } catch (error: PSQLException) {
                    Times.update ({(Times.pollId eq pollId) eq (Times.voterId eq voter.id)}) {
                        it[Times.time] = Json.encodeToString(time)
                    }
                }
            }
        }
    }

    override fun putPollCreationTimes(pollId: PollID, voter: PollVoter, time: PollCreationTime) {
        transaction(database) {
            try {
                Times.insert {
                    it[Times.pollId] = pollId
                    it[voterId] = voter.id
                    it[Times.time] = Json.encodeToString(time)
                }
            } catch (error: PSQLException) {
                Times.update ({(Times.pollId eq pollId) eq (Times.voterId eq voter.id)}) {
                    it[Times.time] = Json.encodeToString(time)
                }
            }
        }
    }

    override fun getPollCreationTimes(pollId: PollID): MutableMap<PollVoter, PollCreationTime> {
        return transaction(database) {
            val result = mutableMapOf<PollVoter, PollCreationTime>()
            val resultJson = Times.select {
                Times.pollId eq pollId
            }.map {
                it[Times.voterId] to it[Times.time]
            }
            for (pair in resultJson) {
                val voterId = pair.first
                val time = pair.second
                result[PollVoter(voterId)] = Json.decodeFromString(time)
            }
            result
        }
    }
}