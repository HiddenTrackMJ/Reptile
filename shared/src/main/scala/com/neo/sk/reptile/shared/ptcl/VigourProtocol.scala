package com.neo.sk.reptile.shared.ptcl

/**
  * Created by liuziwei on 2017/5/5.
  */
object VigourProtocol {


  final case class GroupVigour(
    gid: Long,
    name: String,
    vigour: Double,
    coin: Int
  )

  final case class GroupVigourListRsp(
    groupVigourSeq: Seq[GroupVigour],
    override val errCode: Int = 0,
    override val msg: String = "ok"
  ) extends CommonRsp


  final case class MemberVigour(
    uid: Long,
    username: String,
    nickname: String,
    headImg: String,
    vigour: Double
  )

  final case class MemberVigourListRsp(
    memberVigourSeq: Seq[MemberVigour],
    override val errCode: Int = 0,
    override val msg: String = "ok"
  ) extends CommonRsp


  final case class IssueInfo(
    pid: Long,
    num: Long,
    projectName: String,
    title: String,
    content: String,
    gid: Long,
    author: String,
    authorId: Long,
    issueType: String,
    score: Int,
    postTime: Long
  )

  final case class IssueInfoRsp(
    issueInfo: IssueInfo,
    override val errCode: Int = 0,
    override val msg: String = "ok"
  ) extends CommonRsp

  final case class IssueListRsp(
    issueInfos: Seq[IssueInfo],
    override val errCode: Int = 0,
    override val msg: String = "ok"
  ) extends CommonRsp


  final case class GroupDetail(
    gid: Long,
    name: String,
    desc: String,
    vigour: Double,
    score: Int,
    inIssueList: Seq[IssueInfo],
    outIssueList: Seq[IssueInfo]
  )

  final case class GroupDetailRsp(
    detail: GroupDetail,
    override val errCode: Int = 0,
    override val msg: String = "ok"
  ) extends CommonRsp


  final case class MemberDetail(
    uid: Long,
    username: String,
    nickname: String,
    headImg: String,
    sex: Int,
    gid: Long,
    vigour: Double,
    issueList: Seq[IssueInfo]
  )

  final case class MemberDetailRsp(
    detail: MemberDetail,
    override val errCode: Int = 0,
    override val msg: String = "ok"
  ) extends CommonRsp


  /*
      pid: Long,
    num: Long,
    projectName: String,
    title: String,
    content: String,
    gid: Long,
    author: String,
    authorId: Long,
    issueType: String,
    score: Int,
    postTime: Long
   */

  val emptyIssueInfo = IssueInfo(-1l, -1l, "", "", "", -1l, "", -1l, "", 0, 0l)

  val emptyGroupVigour = GroupVigour(-1l, "", 0.0, 0)

  val emptyGroupDetail = GroupDetail(-1, "", "", 0.0, 0, Nil, Nil)

  val emptyVigour = MemberVigour(-1, "", "", "", 0.0)

  val emptyDetail = MemberDetail(-1, "", "", "", -1, -1, 0.0, Nil)
}





