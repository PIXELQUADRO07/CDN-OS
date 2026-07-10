package org.cdnos.privacy.firewall

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.cdnos.privacy.R

class FirewallAdapter(
    private var apps: List<FirewallAppModel>,
    private val onUpdate: (FirewallAppModel) -> Unit
) : RecyclerView.Adapter<FirewallAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.app_icon)
        val name: TextView = view.findViewById(R.id.app_name)
        val pkg: TextView = view.findViewById(R.id.app_package)
        val wifiBtn: ImageButton = view.findViewById(R.id.btn_wifi)
        val mobileBtn: ImageButton = view.findViewById(R.id.btn_mobile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_firewall_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.icon.setImageDrawable(app.icon)
        holder.name.text = app.appName
        holder.pkg.text = app.packageName
        
        updateButtonState(holder.wifiBtn, app.rule.wifiAllowed)
        updateButtonState(holder.mobileBtn, app.rule.mobileAllowed)

        holder.wifiBtn.setOnClickListener {
            app.rule.wifiAllowed = !app.rule.wifiAllowed
            updateButtonState(holder.wifiBtn, app.rule.wifiAllowed)
            onUpdate(app)
        }

        holder.mobileBtn.setOnClickListener {
            app.rule.mobileAllowed = !app.rule.mobileAllowed
            updateButtonState(holder.mobileBtn, app.rule.mobileAllowed)
            onUpdate(app)
        }
    }

    private fun updateButtonState(btn: ImageButton, allowed: Boolean) {
        if (allowed) {
            btn.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
        } else {
            btn.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
        }
    }

    override fun getItemCount() = apps.size

    fun updateData(newApps: List<FirewallAppModel>) {
        apps = newApps
        notifyDataSetChanged()
    }
}
