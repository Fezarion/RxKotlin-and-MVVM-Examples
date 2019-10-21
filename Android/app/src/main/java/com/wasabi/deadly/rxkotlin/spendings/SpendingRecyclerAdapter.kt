package com.wasabi.deadly.rxkotlin.spendings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.wasabi.deadly.rxkotlin.R

class SpendingRecyclerAdapter(private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<SpendingRecyclerAdapter.SpendingViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(spending: Spending)
    }

    // Current context
    private var context: Context? = null
    // Spending to display
    private var spending = listOf<Spending>()
    // Ids to ignore animations
    private var animatedIds = mutableSetOf<Int>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SpendingViewHolder {
        context = parent.context
        return SpendingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.spending_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return spending.size
    }

    override fun onBindViewHolder(holder: SpendingViewHolder, position: Int) {
        val currentSpending = spending[position]

        holder.textViewTitle.text = currentSpending.title
        holder.textViewValue.text = context?.getString(
            R.string.money_value,
            currentSpending.value
        )
        holder.textViewDescription.text = currentSpending.description

        holder.setOnItemClickListener(currentSpending, onItemClickListener)

        animate(holder.container, currentSpending)
    }

    /**
     * @param spending list to update
     */
    fun setSpending(spending: List<Spending>) {
        this.spending = spending
        notifyDataSetChanged()
    }

    /**
     * @param position to retrieve
     */
    fun getSpendingAt(position: Int): Spending{
        return spending[position]
    }

    /**
     * @param id of spending to remove
     * @see animatedIds
     */
    fun removeIgnoreAnimationId(id: Int) {
        animatedIds.remove(id)
    }

    /**
     * Remove all animatedIds, i.e. show all animation when view is displayed
     * @see animatedIds
     */
    fun resetAnimation() {
        animatedIds.clear()
    }

    /**
     * @param viewGroup to animate
     * @see animatedIds
     */
    private fun animate(viewGroup: ViewGroup, currentSpending: Spending) {
        if (animatedIds.contains(currentSpending.id)) return

        animatedIds.add(currentSpending.id)

        val duration = 1000L

        val fade = AlphaAnimation(0f,1f)
        fade.duration = duration

        val translate = TranslateAnimation(0f,0f,100f,0f)
        translate.duration = duration

        val animationSet = AnimationSet(false)
        animationSet.addAnimation(fade)
        animationSet.addAnimation(translate)

        viewGroup.startAnimation(animationSet)
    }

    /**
     * ViewHolder to display
     */
    class SpendingViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val container: CardView = itemView.findViewById(R.id.container)
        val textViewTitle = itemView.findViewById<TextView>(R.id.text_view_title)!!
        val textViewValue = itemView.findViewById<TextView>(R.id.text_view_value)!!
        val textViewDescription = itemView.findViewById<TextView>(R.id.text_view_description)!!

        fun setOnItemClickListener(spending: Spending, onItemClickListener: OnItemClickListener) {
            view.setOnClickListener {
                onItemClickListener.onItemClick(spending)
            }
        }
    }
}