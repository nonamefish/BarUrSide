package com.mingyuwu.barurside.discoverdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.fromHtml
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mingyuwu.barurside.R
import com.mingyuwu.barurside.data.Notification
import com.mingyuwu.barurside.databinding.ItemNotificationBinding
import com.mingyuwu.barurside.util.Util
import com.mingyuwu.barurside.util.Util.getDiffDay
import com.mingyuwu.barurside.util.Util.getDiffHour
import com.mingyuwu.barurside.util.Util.getDiffMinute
import java.sql.Timestamp

class NotificationAdapter(val viewModel: DiscoverDetailViewModel) :
    ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback) {

    class NotificationViewHolder(private var binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): NotificationViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemNotificationBinding.inflate(layoutInflater, parent, false)

                return NotificationViewHolder(binding)
            }
        }

        fun bind(notification: Notification, viewModel: DiscoverDetailViewModel) {
            binding.imgRatingUser.apply {
                Glide.with(context)
                    .load(notification.image)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(this)
            }

            binding.txtNotifyTitle.text = fromHtml(notification.content, HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.txtNotifyPeriod.text = getNotificationPeriod(notification.timestamp ?: return)
            binding.clFriend.visibility =
                if (notification.type == "friend") View.VISIBLE else View.GONE
            binding.btnConfirm.apply {
                setOnClickListener { viewModel.replyAddFriend(notification, true) }
                visibility = if (notification.reply == null) View.VISIBLE else View.GONE
            }
            binding.btnCancel.apply {
                setOnClickListener { viewModel.replyAddFriend(notification, false) }
                visibility = if (notification.reply == null) View.VISIBLE else View.GONE
            }
            binding.txtReplyInvitation.apply {
                visibility = if (notification.reply == null) View.VISIBLE else View.GONE
                text = when (notification.reply) {
                    true -> Util.getString(R.string.accept_invitation)
                    false -> Util.getString(R.string.reject_invitation)
                    else -> ""
                }
            }
        }

        fun getNotificationPeriod(date: Timestamp): String {
            val diffHour = getDiffHour(date)
            val diffDay = getDiffDay(date)
            val diffWeek = (diffDay.toDouble() / 7).toString().substringBefore(".").toInt()
            return when {
                diffHour < 0 -> Util.getString(R.string.minute_ago, getDiffMinute(date))
                diffHour < 24 -> Util.getString(R.string.hour_ago, diffHour)
                diffDay < 7 -> Util.getString(R.string.day_ago, diffDay)
                diffDay < 30 -> Util.getString(R.string.week_ago, diffWeek)
                else -> Util.getString(R.string.month_ago)
            }
        }
    }

    // Allows the RecyclerView to determine which items have changed when the [List] of [Notification] has been updated.
    companion object DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return (oldItem as Notification).id == (newItem as Notification).id
        }
    }

    // Create new [RecyclerView] item views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {

        return NotificationViewHolder.from(parent)
    }

    // Replaces the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NotificationViewHolder -> {
                holder.bind((getItem(position) as Notification), viewModel)
            }
        }
    }
}
